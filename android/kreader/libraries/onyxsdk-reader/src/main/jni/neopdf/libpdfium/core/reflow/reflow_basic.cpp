#include "include/reflow_basic.h"

#include "../fxcrt/plex.h"

CFX_GrowOnlyPool::CFX_GrowOnlyPool(size_t trunk_size)
{
    m_TrunkSize = trunk_size;
    m_pFirstTrunk = NULL;
}
CFX_GrowOnlyPool::~CFX_GrowOnlyPool()
{
    FreeAll();
}
struct _FX_GrowOnlyTrunk {
    size_t	m_Size;
    size_t	m_Allocated;
    _FX_GrowOnlyTrunk*	m_pNext;
};
void CFX_GrowOnlyPool::FreeAll()
{
    _FX_GrowOnlyTrunk* pTrunk = (_FX_GrowOnlyTrunk*)m_pFirstTrunk;
    while (pTrunk) {
        _FX_GrowOnlyTrunk* pNext = pTrunk->m_pNext;
        FX_Free(pTrunk);
        pTrunk = pNext;
    }
    m_pFirstTrunk = NULL;
}
void* CFX_GrowOnlyPool::Alloc(size_t size)
{
    size = (size + 3) / 4 * 4;
    _FX_GrowOnlyTrunk* pTrunk = (_FX_GrowOnlyTrunk*)m_pFirstTrunk;
    while (pTrunk) {
        if (pTrunk->m_Size - pTrunk->m_Allocated >= size) {
            void* p = (uint8_t*)(pTrunk + 1) + pTrunk->m_Allocated;
            pTrunk->m_Allocated += size;
            return p;
        }
        pTrunk = pTrunk->m_pNext;
    }
    size_t alloc_size = size > m_TrunkSize ? size : m_TrunkSize;
    pTrunk = (_FX_GrowOnlyTrunk*)FX_Alloc(uint8_t, sizeof(_FX_GrowOnlyTrunk) + alloc_size);
    pTrunk->m_Size = alloc_size;
    pTrunk->m_Allocated = size;
    pTrunk->m_pNext = (_FX_GrowOnlyTrunk*)m_pFirstTrunk;
    m_pFirstTrunk = pTrunk;
    return pTrunk + 1;
}

void FX_PRIVATEDATA::FreeData()
{
    if (m_pData == NULL) {
        return;
    }
    if (m_bSelfDestruct) {
        delete (CFX_DestructObject*)m_pData;
    } else if (m_pCallback) {
        m_pCallback(m_pData);
    }
}

CFX_PrivateData::~CFX_PrivateData()
{
    ClearAll();
}
void CFX_PrivateData::AddData(void* pModuleId, void* pData, PD_CALLBACK_FREEDATA callback, FX_BOOL bSelfDestruct)
{
    if (pModuleId == NULL) {
        return;
    }
    FX_PRIVATEDATA* pList = m_DataList.GetData();
    int count = m_DataList.GetSize();
    for (int i = 0; i < count; i ++) {
        if (pList[i].m_pModuleId == pModuleId) {
            pList[i].FreeData();
            pList[i].m_pData = pData;
            pList[i].m_pCallback = callback;
            return;
        }
    }
    FX_PRIVATEDATA data = {pModuleId, pData, callback, bSelfDestruct};
    m_DataList.Add(data);
}
void CFX_PrivateData::SetPrivateData(void* pModuleId, void* pData, PD_CALLBACK_FREEDATA callback)
{
    AddData(pModuleId, pData, callback, FALSE);
}
void CFX_PrivateData::SetPrivateObj(void* pModuleId, CFX_DestructObject* pObj)
{
    AddData(pModuleId, pObj, NULL, TRUE);
}
FX_BOOL CFX_PrivateData::RemovePrivateData(void* pModuleId)
{
    if (pModuleId == NULL) {
        return FALSE;
    }
    FX_PRIVATEDATA* pList = m_DataList.GetData();
    int count = m_DataList.GetSize();
    for (int i = 0; i < count; i ++) {
        if (pList[i].m_pModuleId == pModuleId) {
            m_DataList.RemoveAt(i);
            return TRUE;
        }
    }
    return FALSE;
}
void* CFX_PrivateData::GetPrivateData(void* pModuleId)
{
    if (pModuleId == NULL) {
        return NULL;
    }
    FX_PRIVATEDATA* pList = m_DataList.GetData();
    int count = m_DataList.GetSize();
    for (int i = 0; i < count; i ++) {
        if (pList[i].m_pModuleId == pModuleId) {
            return pList[i].m_pData;
        }
    }
    return NULL;
}
void CFX_PrivateData::ClearAll()
{
    FX_PRIVATEDATA* pList = m_DataList.GetData();
    int count = m_DataList.GetSize();
    for (int i = 0; i < count; i ++) {
        pList[i].FreeData();
    }
    m_DataList.RemoveAll();
}

CFX_BaseSegmentedArray::CFX_BaseSegmentedArray(int unit_size, int segment_units, int index_size)
    : m_UnitSize(unit_size)
    , m_SegmentSize(segment_units)
    , m_IndexSize(index_size)
    , m_IndexDepth(0)
    , m_DataSize(0)
    , m_pIndex(NULL)
{
}
void CFX_BaseSegmentedArray::SetUnitSize(int unit_size, int segment_units, int index_size)
{
    ASSERT(m_DataSize == 0);
    m_UnitSize = unit_size;
    m_SegmentSize = segment_units;
    m_IndexSize = index_size;
}
CFX_BaseSegmentedArray::~CFX_BaseSegmentedArray()
{
    RemoveAll();
}
static void _ClearIndex(int level, int size, void** pIndex)
{
    if (level == 0) {
        FX_Free(pIndex);
        return;
    }
    for (int i = 0; i < size; i++) {
        if (pIndex[i] == NULL) {
            continue;
        }
        _ClearIndex(level - 1, size, (void**)pIndex[i]);
    }
    FX_Free(pIndex);
}
void CFX_BaseSegmentedArray::RemoveAll()
{
    if (m_pIndex == NULL) {
        return;
    }
    _ClearIndex(m_IndexDepth, m_IndexSize, (void**)m_pIndex);
    m_pIndex = NULL;
    m_IndexDepth = 0;
    m_DataSize = 0;
}
void* CFX_BaseSegmentedArray::Add()
{
    if (m_DataSize % m_SegmentSize) {
        return GetAt(m_DataSize ++);
    }
    void* pSegment = FX_Alloc2D(uint8_t, m_UnitSize, m_SegmentSize);
    if (m_pIndex == NULL) {
        m_pIndex = pSegment;
        m_DataSize ++;
        return pSegment;
    }
    if (m_IndexDepth == 0) {
        void** pIndex = (void**)FX_Alloc(void*, m_IndexSize);
        pIndex[0] = m_pIndex;
        pIndex[1] = pSegment;
        m_pIndex = pIndex;
        m_DataSize ++;
        m_IndexDepth ++;
        return pSegment;
    }
    int seg_index = m_DataSize / m_SegmentSize;
    if (seg_index % m_IndexSize) {
        void** pIndex = GetIndex(seg_index);
        pIndex[seg_index % m_IndexSize] = pSegment;
        m_DataSize ++;
        return pSegment;
    }
    int tree_size = 1;
    int i;
    for (i = 0; i < m_IndexDepth; i ++) {
        tree_size *= m_IndexSize;
    }
    if (m_DataSize == tree_size * m_SegmentSize) {
        void** pIndex = (void**)FX_Alloc(void*, m_IndexSize);
        pIndex[0] = m_pIndex;
        m_pIndex = pIndex;
        m_IndexDepth ++;
    } else {
        tree_size /= m_IndexSize;
    }
    void** pSpot = (void**)m_pIndex;
    for (i = 1; i < m_IndexDepth; i ++) {
        if (pSpot[seg_index / tree_size] == NULL) {
            pSpot[seg_index / tree_size] = (void*)FX_Alloc(void*, m_IndexSize);
        }
        pSpot = (void**)pSpot[seg_index / tree_size];
        seg_index = seg_index % tree_size;
        tree_size /= m_IndexSize;
    }
    if (i < m_IndexDepth) {
        FX_Free(pSegment);
        RemoveAll();
        return NULL;
    }
    pSpot[seg_index % m_IndexSize] = pSegment;
    m_DataSize ++;
    return pSegment;
}
void** CFX_BaseSegmentedArray::GetIndex(int seg_index) const
{
    ASSERT(m_IndexDepth != 0);
    if (m_IndexDepth == 1) {
        return (void**)m_pIndex;
    } else if (m_IndexDepth == 2) {
        return (void**)((void**)m_pIndex)[seg_index / m_IndexSize];
    }
    int tree_size = 1;
    int i;
    for (i = 1; i < m_IndexDepth; i ++) {
        tree_size *= m_IndexSize;
    }
    void** pSpot = (void**)m_pIndex;
    for (i = 1; i < m_IndexDepth; i ++) {
        pSpot = (void**)pSpot[seg_index / tree_size];
        seg_index = seg_index % tree_size;
        tree_size /= m_IndexSize;
    }
    return pSpot;
}
void* CFX_BaseSegmentedArray::IterateSegment(const uint8_t* pSegment, int count, FX_BOOL (*callback)(void* param, void* pData), void* param) const
{
    for (int i = 0; i < count; i ++) {
        if (!callback(param, (void*)(pSegment + i * m_UnitSize))) {
            return (void*)(pSegment + i * m_UnitSize);
        }
    }
    return NULL;
}
void* CFX_BaseSegmentedArray::IterateIndex(int level, int& start, void** pIndex, FX_BOOL (*callback)(void* param, void* pData), void* param) const
{
    if (level == 0) {
        int count = m_DataSize - start;
        if (count > m_SegmentSize) {
            count = m_SegmentSize;
        }
        start += count;
        return IterateSegment((const uint8_t*)pIndex, count, callback, param);
    }
    for (int i = 0; i < m_IndexSize; i ++) {
        if (pIndex[i] == NULL) {
            continue;
        }
        void* p = IterateIndex(level - 1, start, (void**)pIndex[i], callback, param);
        if (p) {
            return p;
        }
    }
    return NULL;
}
void* CFX_BaseSegmentedArray::Iterate(FX_BOOL (*callback)(void* param, void* pData), void* param) const
{
    if (m_pIndex == NULL) {
        return NULL;
    }
    int start = 0;
    return IterateIndex(m_IndexDepth, start, (void**)m_pIndex, callback, param);
}
void* CFX_BaseSegmentedArray::GetAt(int index) const
{
    if (index < 0 || index >= m_DataSize) {
        return NULL;
    }
    if (m_IndexDepth == 0) {
        return (uint8_t*)m_pIndex + m_UnitSize * index;
    }
    int seg_index = index / m_SegmentSize;
    return (uint8_t*)GetIndex(seg_index)[seg_index % m_IndexSize] + (index % m_SegmentSize) * m_UnitSize;
}
void CFX_BaseSegmentedArray::Delete(int index, int count)
{
    if(index < 0 || count < 1 || index + count > m_DataSize) {
        return;
    }
    int i;
    for (i = index; i < m_DataSize - count; i ++) {
        uint8_t* pSrc = (uint8_t*)GetAt(i + count);
        uint8_t* pDest = (uint8_t*)GetAt(i);
        for (int j = 0; j < m_UnitSize; j ++) {
            pDest[j] = pSrc[j];
        }
    }
    int new_segs = (m_DataSize - count + m_SegmentSize - 1) / m_SegmentSize;
    int old_segs = (m_DataSize + m_SegmentSize - 1) / m_SegmentSize;
    if (new_segs < old_segs) {
        if(m_IndexDepth) {
            for (i = new_segs; i < old_segs; i ++) {
                void** pIndex = GetIndex(i);
                FX_Free(pIndex[i % m_IndexSize]);
                pIndex[i % m_IndexSize] = NULL;
            }
        } else {
            FX_Free(m_pIndex);
            m_pIndex = NULL;
        }
    }
    m_DataSize -= count;
}

CPDF_PageObjects::CPDF_PageObjects(FX_BOOL bReleaseMembers) : m_ObjectList(128)
{
    m_bBackgroundAlphaNeeded = FALSE;
    m_bReleaseMembers = bReleaseMembers;
    m_ParseState = PDF_CONTENT_NOT_PARSED;
    m_pParser = NULL;
    m_pFormStream = NULL;
    m_pResources = NULL;
}
CPDF_PageObjects::~CPDF_PageObjects()
{
    if (m_pParser) {
        delete m_pParser;
    }
    if (!m_bReleaseMembers) {
        return;
    }
    FX_POSITION pos = m_ObjectList.GetHeadPosition();
    while (pos) {
        delete (CPDF_PageObject*)m_ObjectList.GetNext(pos);
    }
}
void CPDF_PageObjects::ContinueParse(IFX_Pause* pPause)
{
    if (m_pParser == NULL) {
        return;
    }
    m_pParser->Continue(pPause);
    if (m_pParser->GetStatus() == CPDF_ContentParser::Done) {
        m_ParseState = PDF_CONTENT_PARSED;
        delete m_pParser;
        m_pParser = NULL;
    }
}
int CPDF_PageObjects::EstimateParseProgress() const
{
    if (m_pParser == NULL) {
        return m_ParseState == PDF_CONTENT_PARSED ? 100 : 0;
    }
    return m_pParser->EstimateProgress();
}
FX_POSITION CPDF_PageObjects::InsertObject(FX_POSITION posInsertAfter, CPDF_PageObject* pNewObject)
{
    if (posInsertAfter == NULL) {
        return m_ObjectList.AddHead(pNewObject);
    } else {
        return m_ObjectList.InsertAfter(posInsertAfter, pNewObject);
    }
}
int CPDF_PageObjects::GetObjectIndex(CPDF_PageObject* pObj) const
{
    int index = 0;
    FX_POSITION pos = m_ObjectList.GetHeadPosition();
    while (pos) {
        CPDF_PageObject* pThisObj = (CPDF_PageObject*)m_ObjectList.GetNext(pos);
        if (pThisObj == pObj) {
            return index;
        }
        index ++;
    }
    return -1;
}
CPDF_PageObject* CPDF_PageObjects::GetObjectByIndex(int index) const
{
    FX_POSITION pos = m_ObjectList.FindIndex(index);
    if (pos == NULL) {
        return NULL;
    }
    return (CPDF_PageObject*)m_ObjectList.GetAt(pos);
}
void CPDF_PageObjects::Transform(const CFX_AffineMatrix& matrix)
{
    FX_POSITION pos = m_ObjectList.GetHeadPosition();
    while (pos) {
        CPDF_PageObject* pObj = (CPDF_PageObject*)m_ObjectList.GetNext(pos);
        pObj->Transform(matrix);
    }
}
CFX_FloatRect CPDF_PageObjects::CalcBoundingBox() const
{
    if (m_ObjectList.GetCount() == 0) {
        return CFX_FloatRect(0, 0, 0, 0);
    }
    FX_FLOAT left, right, top, bottom;
    left = bottom = 1000000 * 1.0f;
    right = top = -1000000 * 1.0f;
    FX_POSITION pos = m_ObjectList.GetHeadPosition();
    while (pos) {
        CPDF_PageObject* pObj = (CPDF_PageObject*)m_ObjectList.GetNext(pos);
        if (left > pObj->m_Left) {
            left = pObj->m_Left;
        }
        if (right < pObj->m_Right) {
            right = pObj->m_Right;
        }
        if (top < pObj->m_Top) {
            top = pObj->m_Top;
        }
        if (bottom > pObj->m_Bottom) {
            bottom = pObj->m_Bottom;
        }
    }
    return CFX_FloatRect(left, bottom, right, top);
}
void CPDF_PageObjects::LoadTransInfo()
{
    if (m_pFormDict == NULL) {
        return;
    }
    CPDF_Dictionary* pGroup = m_pFormDict->GetDict(FX_BSTRC("Group"));
    if (pGroup == NULL) {
        return;
    }
    if (pGroup->GetString(FX_BSTRC("S")) != FX_BSTRC("Transparency")) {
        return;
    }
    m_Transparency |= PDFTRANS_GROUP;
    if (pGroup->GetInteger(FX_BSTRC("I"))) {
        m_Transparency |= PDFTRANS_ISOLATED;
    }
    if (pGroup->GetInteger(FX_BSTRC("K"))) {
        m_Transparency |= PDFTRANS_KNOCKOUT;
    }
}
void CPDF_PageObjects::ClearCacheObjects()
{
    m_ParseState = PDF_CONTENT_NOT_PARSED;
    if (m_pParser) {
        delete m_pParser;
    }
    m_pParser = NULL;
    if (m_bReleaseMembers) {
        FX_POSITION pos = m_ObjectList.GetHeadPosition();
        while (pos) {
            delete (CPDF_PageObject*)m_ObjectList.GetNext(pos);
        }
    }
    m_ObjectList.RemoveAll();
}

#ifndef PDF_ENABLE_XFA

CFX_MapPtrToPtr::CFX_MapPtrToPtr(int nBlockSize)
    : m_pHashTable(nullptr),
      m_nHashTableSize(17),
      m_nCount(0),
      m_pFreeList(nullptr),
      m_pBlocks(nullptr),
      m_nBlockSize(nBlockSize) {
  ASSERT(m_nBlockSize > 0);
}

void CFX_MapPtrToPtr::RemoveAll() {
  FX_Free(m_pHashTable);
  m_pHashTable = nullptr;
  m_nCount = 0;
  m_pFreeList = nullptr;
  if (m_pBlocks) {
    m_pBlocks->FreeDataChain();
    m_pBlocks = nullptr;
  }
}

CFX_MapPtrToPtr::~CFX_MapPtrToPtr() {
  RemoveAll();
  ASSERT(m_nCount == 0);
}
uint32_t CFX_MapPtrToPtr::HashKey(void* key) const {
  return ((uint32_t)(uintptr_t)key) >> 4;
}
void CFX_MapPtrToPtr::GetNextAssoc(FX_POSITION& rNextPosition,
                                   void*& rKey,
                                   void*& rValue) const {
  ASSERT(m_pHashTable);
  CAssoc* pAssocRet = (CAssoc*)rNextPosition;
  ASSERT(pAssocRet);
  if (pAssocRet == (CAssoc*)-1) {
    for (uint32_t nBucket = 0; nBucket < m_nHashTableSize; nBucket++) {
      pAssocRet = m_pHashTable[nBucket];
      if (pAssocRet)
        break;
    }
    ASSERT(pAssocRet);
  }
  CAssoc* pAssocNext = pAssocRet->pNext;
  for (uint32_t nBucket = (HashKey(pAssocRet->key) % m_nHashTableSize) + 1;
       nBucket < m_nHashTableSize && !pAssocNext; nBucket++) {
    pAssocNext = m_pHashTable[nBucket];
  }
  rNextPosition = (FX_POSITION)pAssocNext;
  rKey = pAssocRet->key;
  rValue = pAssocRet->value;
}
FX_BOOL CFX_MapPtrToPtr::Lookup(void* key, void*& rValue) const {
  uint32_t nHash;
  CAssoc* pAssoc = GetAssocAt(key, nHash);
  if (!pAssoc) {
    return FALSE;
  }
  rValue = pAssoc->value;
  return TRUE;
}

void* CFX_MapPtrToPtr::GetValueAt(void* key) const {
  uint32_t nHash;
  CAssoc* pAssoc = GetAssocAt(key, nHash);
  return pAssoc ? pAssoc->value : nullptr;
}

void*& CFX_MapPtrToPtr::operator[](void* key) {
  uint32_t nHash;
  CAssoc* pAssoc = GetAssocAt(key, nHash);
  if (!pAssoc) {
    if (!m_pHashTable)
      InitHashTable(m_nHashTableSize);
    pAssoc = NewAssoc();
    pAssoc->key = key;
    pAssoc->pNext = m_pHashTable[nHash];
    m_pHashTable[nHash] = pAssoc;
  }
  return pAssoc->value;
}
CFX_MapPtrToPtr::CAssoc* CFX_MapPtrToPtr::GetAssocAt(void* key,
                                                     uint32_t& nHash) const {
  nHash = HashKey(key) % m_nHashTableSize;
  if (!m_pHashTable) {
    return nullptr;
  }
  CAssoc* pAssoc;
  for (pAssoc = m_pHashTable[nHash]; pAssoc; pAssoc = pAssoc->pNext) {
    if (pAssoc->key == key)
      return pAssoc;
  }
  return nullptr;
}
CFX_MapPtrToPtr::CAssoc* CFX_MapPtrToPtr::NewAssoc() {
  if (!m_pFreeList) {
    CFX_Plex* newBlock = CFX_Plex::Create(m_pBlocks, m_nBlockSize,
                                          sizeof(CFX_MapPtrToPtr::CAssoc));
    CFX_MapPtrToPtr::CAssoc* pAssoc =
        (CFX_MapPtrToPtr::CAssoc*)newBlock->data();
    pAssoc += m_nBlockSize - 1;
    for (int i = m_nBlockSize - 1; i >= 0; i--, pAssoc--) {
      pAssoc->pNext = m_pFreeList;
      m_pFreeList = pAssoc;
    }
  }
  CFX_MapPtrToPtr::CAssoc* pAssoc = m_pFreeList;
  m_pFreeList = m_pFreeList->pNext;
  m_nCount++;
  ASSERT(m_nCount > 0);
  pAssoc->key = 0;
  pAssoc->value = 0;
  return pAssoc;
}
void CFX_MapPtrToPtr::InitHashTable(uint32_t nHashSize, FX_BOOL bAllocNow) {
  ASSERT(m_nCount == 0);
  ASSERT(nHashSize > 0);
  FX_Free(m_pHashTable);
  m_pHashTable = nullptr;
  if (bAllocNow) {
    m_pHashTable = FX_Alloc(CAssoc*, nHashSize);
  }
  m_nHashTableSize = nHashSize;
}
FX_BOOL CFX_MapPtrToPtr::RemoveKey(void* key) {
  if (!m_pHashTable) {
    return FALSE;
  }
  CAssoc** ppAssocPrev;
  ppAssocPrev = &m_pHashTable[HashKey(key) % m_nHashTableSize];
  CAssoc* pAssoc;
  for (pAssoc = *ppAssocPrev; pAssoc; pAssoc = pAssoc->pNext) {
    if (pAssoc->key == key) {
      *ppAssocPrev = pAssoc->pNext;
      FreeAssoc(pAssoc);
      return TRUE;
    }
    ppAssocPrev = &pAssoc->pNext;
  }
  return FALSE;
}
void CFX_MapPtrToPtr::FreeAssoc(CFX_MapPtrToPtr::CAssoc* pAssoc) {
  pAssoc->pNext = m_pFreeList;
  m_pFreeList = pAssoc;
  m_nCount--;
  ASSERT(m_nCount >= 0);
  if (m_nCount == 0) {
    RemoveAll();
  }
}

#endif

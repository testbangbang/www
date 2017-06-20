#ifndef REFLOW_BASIC_H
#define REFLOW_BASIC_H

#include "stdint.h"

#include "../../fxcrt/include/fx_basic.h"
#include "../../fpdfapi/fpdf_page/include/cpdf_pageobject.h"
#include "../../fpdfapi/fpdf_page/pageint.h"
#include "../../fpdfapi/fpdf_parser/include/cpdf_stream.h"

#ifndef TRUE
#define TRUE	1
#endif

#ifndef FALSE
#define FALSE	0
#endif

#define PDF_CONTENT_NOT_PARSED	0
#define PDF_CONTENT_PARSING		1
#define PDF_CONTENT_PARSED		2

typedef unsigned short          FX_WORD;     // Keep - "an efficient small type"
typedef unsigned int            FX_DWORD;    // Keep - "an efficient type"
typedef int                     FX_BOOL;     // Keep, sadly not always 0 or 1.

typedef void (*PD_CALLBACK_FREEDATA)(void* pData);

typedef CFX_ArrayTemplate<FX_WORD>		CFX_WordArray;
typedef CFX_ArrayTemplate<void*>		CFX_PtrArray;

#define CFX_AffineMatrix	CFX_Matrix

class CFX_DestructObject
{
public:

    virtual ~CFX_DestructObject() {}
};

class CFX_GrowOnlyPool
{
public:

    CFX_GrowOnlyPool(size_t trunk_size = 16384);

    ~CFX_GrowOnlyPool();

    void	SetTrunkSize(size_t trunk_size)
    {
        m_TrunkSize = trunk_size;
    }

    void*	AllocDebug(size_t size, const FX_CHAR* file, int line)
    {
        return Alloc(size);
    }

    void*	Alloc(size_t size);

    void*	ReallocDebug(void* p, size_t new_size, const FX_CHAR* file, int line)
    {
        return NULL;
    }

    void*	Realloc(void* p, size_t new_size)
    {
        return NULL;
    }

    void	Free(void*) {}

    void	FreeAll();
private:

    size_t	m_TrunkSize;

    void*	m_pFirstTrunk;
};

struct FX_PRIVATEDATA {

    void					FreeData();

    void*				m_pModuleId;

    void*				m_pData;

    PD_CALLBACK_FREEDATA	m_pCallback;

    FX_BOOL					m_bSelfDestruct;
};

class CFX_PrivateData
{
public:

    ~CFX_PrivateData();

    void					ClearAll();

    void					SetPrivateData(void* module_id, void* pData, PD_CALLBACK_FREEDATA callback);

    void					SetPrivateObj(void* module_id, CFX_DestructObject* pObj);

    void*				GetPrivateData(void* module_id);

    FX_BOOL					LookupPrivateData(void* module_id, void* &pData) const
    {
        if (!module_id) {
            return FALSE;
        }
        FX_DWORD nCount = m_DataList.GetSize();
        for (FX_DWORD n = 0; n < nCount; n ++) {
            if (m_DataList[n].m_pModuleId == module_id) {
                pData = m_DataList[n].m_pData;
                return TRUE;
            }
        }
        return FALSE;
    }

    FX_BOOL					RemovePrivateData(void* module_id);
protected:

    CFX_ArrayTemplate<FX_PRIVATEDATA>	m_DataList;

    void					AddData(void* module_id, void* pData, PD_CALLBACK_FREEDATA callback, FX_BOOL bSelfDestruct);
};

class CFX_BaseSegmentedArray
{
public:
    CFX_BaseSegmentedArray(int unit_size = 1, int segment_units = 512, int index_size = 8);

    ~CFX_BaseSegmentedArray();

    void	SetUnitSize(int unit_size, int segment_units, int index_size = 8);

    void*	Add();

    void*	GetAt(int index) const;

    void	RemoveAll();

    void	Delete(int index, int count = 1);

    int		GetSize() const
    {
        return m_DataSize;
    }

    int		GetSegmentSize() const
    {
        return m_SegmentSize;
    }

    int		GetUnitSize() const
    {
        return m_UnitSize;
    }

    void*	Iterate(FX_BOOL (*callback)(void* param, void* pData), void* param) const;
private:

    int				m_UnitSize;

    short			m_SegmentSize;

    uint8_t			m_IndexSize;

    uint8_t			m_IndexDepth;

    int				m_DataSize;

    void*			m_pIndex;
    void**	GetIndex(int seg_index) const;
    void*	IterateIndex(int level, int& start, void** pIndex, FX_BOOL (*callback)(void* param, void* pData), void* param) const;
    void*	IterateSegment(const uint8_t* pSegment, int count, FX_BOOL (*callback)(void* param, void* pData), void* param) const;
};
template <class ElementType>
class CFX_SegmentedArray : public CFX_BaseSegmentedArray
{
public:
    CFX_SegmentedArray(int segment_units, int index_size = 8)
        : CFX_BaseSegmentedArray(sizeof(ElementType), segment_units, index_size)
    {}

    void	Add(ElementType data)
    {
        *(ElementType*)CFX_BaseSegmentedArray::Add() = data;
    }

    ElementType& operator [] (int index)
    {
        return *(ElementType*)CFX_BaseSegmentedArray::GetAt(index);
    }
};

class CPDF_PageObjects
{
public:

    CPDF_PageObjects(FX_BOOL bReleaseMembers = TRUE);

    ~CPDF_PageObjects();




    void				ContinueParse(IFX_Pause* pPause);

    int					GetParseState() const
    {
        return m_ParseState;
    }

    FX_BOOL				IsParsed() const
    {
        return m_ParseState == PDF_CONTENT_PARSED;
    }

    int					EstimateParseProgress() const;




    FX_POSITION			GetFirstObjectPosition() const
    {
        return m_ObjectList.GetHeadPosition();
    }

    FX_POSITION			GetLastObjectPosition() const
    {
        return m_ObjectList.GetTailPosition();
    }

    CPDF_PageObject*	GetNextObject(FX_POSITION& pos) const
    {
        return (CPDF_PageObject*)m_ObjectList.GetNext(pos);
    }

    CPDF_PageObject*	GetPrevObject(FX_POSITION& pos) const
    {
        return (CPDF_PageObject*)m_ObjectList.GetPrev(pos);
    }

    CPDF_PageObject*	GetObjectAt(FX_POSITION pos) const
    {
        return (CPDF_PageObject*)m_ObjectList.GetAt(pos);
    }

    FX_DWORD			CountObjects() const
    {
        return m_ObjectList.GetCount();
    }

    int					GetObjectIndex(CPDF_PageObject* pObj) const;

    CPDF_PageObject*	GetObjectByIndex(int index) const;





    FX_POSITION			InsertObject(FX_POSITION posInsertAfter, CPDF_PageObject* pNewObject);

    void				Transform(const CFX_AffineMatrix& matrix);

    FX_BOOL				BackgroundAlphaNeeded() const
    {
        return m_bBackgroundAlphaNeeded;
    }

    CFX_FloatRect		CalcBoundingBox() const;

    CPDF_Dictionary*	m_pFormDict;

    CPDF_Stream*		m_pFormStream;

    CPDF_Document*		m_pDocument;

    CPDF_Dictionary*	m_pPageResources;

    CPDF_Dictionary*	m_pResources;

    CFX_FloatRect		m_BBox;

    int					m_Transparency;

protected:
    friend class		CPDF_ContentParser;
    friend class		CPDF_StreamContentParser;
    friend class		CPDF_AllStates;

    CFX_PtrList			m_ObjectList;

    FX_BOOL				m_bBackgroundAlphaNeeded;

    FX_BOOL				m_bReleaseMembers;
    void				LoadTransInfo();
    void                ClearCacheObjects();

    CPDF_ContentParser*	m_pParser;

    FX_BOOL				m_ParseState;
};

#ifndef PDF_ENABLE_XFA
class CFX_MapPtrToPtr {
 protected:
  struct CAssoc {
    CAssoc* pNext;
    void* key;
    void* value;
  };

 public:
  CFX_MapPtrToPtr(int nBlockSize = 10);
  ~CFX_MapPtrToPtr();

  int GetCount() const { return m_nCount; }
  bool IsEmpty() const { return m_nCount == 0; }

  FX_BOOL Lookup(void* key, void*& rValue) const;

  void* GetValueAt(void* key) const;

  void*& operator[](void* key);

  void SetAt(void* key, void* newValue) { (*this)[key] = newValue; }

  FX_BOOL RemoveKey(void* key);

  void RemoveAll();

  FX_POSITION GetStartPosition() const {
    return m_nCount == 0 ? nullptr : (FX_POSITION)-1;
  }

  void GetNextAssoc(FX_POSITION& rNextPosition,
                    void*& rKey,
                    void*& rValue) const;

  uint32_t GetHashTableSize() const { return m_nHashTableSize; }

  void InitHashTable(uint32_t hashSize, FX_BOOL bAllocNow = TRUE);

 protected:
  CAssoc** m_pHashTable;

  uint32_t m_nHashTableSize;

  int m_nCount;

  CAssoc* m_pFreeList;

  struct CFX_Plex* m_pBlocks;

  int m_nBlockSize;

  uint32_t HashKey(void* key) const;

  CAssoc* NewAssoc();

  void FreeAssoc(CAssoc* pAssoc);

  CAssoc* GetAssocAt(void* key, uint32_t& hash) const;
};

template <class KeyType, class ValueType>
class CFX_MapPtrTemplate : public CFX_MapPtrToPtr {
 public:
  CFX_MapPtrTemplate() : CFX_MapPtrToPtr(10) {}

  FX_BOOL Lookup(KeyType key, ValueType& rValue) const {
    void* pValue = nullptr;
    if (!CFX_MapPtrToPtr::Lookup((void*)(uintptr_t)key, pValue)) {
      return FALSE;
    }
    rValue = (ValueType)(uintptr_t)pValue;
    return TRUE;
  }

  ValueType& operator[](KeyType key) {
    return (ValueType&)CFX_MapPtrToPtr::operator[]((void*)(uintptr_t)key);
  }

  void SetAt(KeyType key, ValueType newValue) {
    CFX_MapPtrToPtr::SetAt((void*)(uintptr_t)key, (void*)(uintptr_t)newValue);
  }

  FX_BOOL RemoveKey(KeyType key) {
    return CFX_MapPtrToPtr::RemoveKey((void*)(uintptr_t)key);
  }

  void GetNextAssoc(FX_POSITION& rNextPosition,
                    KeyType& rKey,
                    ValueType& rValue) const {
    void* pKey = nullptr;
    void* pValue = nullptr;
    CFX_MapPtrToPtr::GetNextAssoc(rNextPosition, pKey, pValue);
    rKey = (KeyType)(uintptr_t)pKey;
    rValue = (ValueType)(uintptr_t)pValue;
  }
};
#endif  // PDF_ENABLE_XFA

#endif // REFLOW_BASIC_H

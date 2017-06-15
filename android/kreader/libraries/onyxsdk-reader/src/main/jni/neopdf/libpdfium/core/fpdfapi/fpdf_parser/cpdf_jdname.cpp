// Copyright 2016 PDFium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// Original code copyright 2014 Foxit Software Inc. http://www.foxitsoftware.com

#include "core/fpdfapi/fpdf_parser/include/cpdf_jdname.h"

#include "core/fpdfapi/fpdf_parser/include/fpdf_parser_decode.h"

CPDF_JDName::CPDF_JDName(const CFX_ByteString& str) : m_Name(str) {
	m_exType = JDSTREAM;
}

CPDF_JDName::~CPDF_JDName() {}

CPDF_Object::Type CPDF_JDName::GetType() const {
  return JDSTREAM;
}

CPDF_Object::Type CPDF_JDName::GetExType() const {
	return m_exType;
}
void CPDF_JDName::SetExType(Type type) const{
	m_exType = type;
}


CPDF_Object* CPDF_JDName::Clone() const {
  return new CPDF_JDName(m_Name);
}

CFX_ByteString CPDF_JDName::GetString() const {
  return m_Name;
}

void CPDF_JDName::SetString(const CFX_ByteString& str) {
  m_Name = str;
}

bool CPDF_JDName::IsName() const {
  return true;
}

CPDF_JDName* CPDF_JDName::AsName() {
  return this;
}

const CPDF_JDName* CPDF_JDName::AsName() const {
  return this;
}

CFX_WideString CPDF_JDName::GetUnicodeText() const {
  return PDF_DecodeText(m_Name);
}

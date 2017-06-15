// Copyright 2016 PDFium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// Original code copyright 2014 Foxit Software Inc. http://www.foxitsoftware.com

#ifndef CORE_FPDFAPI_FPDF_PARSER_INCLUDE_CPDF_JDNAME_H_
#define CORE_FPDFAPI_FPDF_PARSER_INCLUDE_CPDF_JDNAME_H_

#include "core/fpdfapi/fpdf_parser/include/cpdf_object.h"

class CPDF_JDName : public CPDF_Object {
 public:
  explicit CPDF_JDName(const CFX_ByteString& str);

  // CPDF_Object.
  Type GetType() const override;
  Type GetExType() const override;
  void SetExType(Type) const override;
  CPDF_Object* Clone() const override;
  CFX_ByteString GetString() const override;
  CFX_WideString GetUnicodeText() const override;
  void SetString(const CFX_ByteString& str) override;
  bool IsName() const override;
  CPDF_JDName* AsName() override;
  const CPDF_JDName* AsName() const override;

 protected:
  ~CPDF_JDName() override;

  CFX_ByteString m_Name;
};

#endif  // CORE_FPDFAPI_FPDF_PARSER_INCLUDE_CPDF_JDNAME_H_

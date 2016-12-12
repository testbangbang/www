// Copyright 2016 PDFium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "core/fxcrt/include/cfx_observable.h"

#include <utility>

#include "testing/fx_string_testhelpers.h"
#include "testing/gtest/include/gtest/gtest.h"

namespace {

class PseudoObservable : public CFX_Observable<PseudoObservable> {
 public:
  PseudoObservable() {}
  int SomeMethod() { return 42; }
  size_t ActiveObservedPtrs() const { return ActiveObservedPtrsForTesting(); }
};

}  // namespace

TEST(fxcrt, ObservePtrNull) {
  PseudoObservable::ObservedPtr ptr;
  EXPECT_EQ(nullptr, ptr.Get());
}

TEST(fxcrt, ObservePtrLivesLonger) {
  PseudoObservable* pObs = new PseudoObservable;
  PseudoObservable::ObservedPtr ptr(pObs);
  EXPECT_NE(nullptr, ptr.Get());
  EXPECT_EQ(1u, pObs->ActiveObservedPtrs());
  delete pObs;
  EXPECT_EQ(nullptr, ptr.Get());
}

TEST(fxcrt, ObservePtrLivesShorter) {
  PseudoObservable obs;
  {
    PseudoObservable::ObservedPtr ptr(&obs);
    EXPECT_NE(nullptr, ptr.Get());
    EXPECT_EQ(1u, obs.ActiveObservedPtrs());
  }
  EXPECT_EQ(0u, obs.ActiveObservedPtrs());
}

TEST(fxcrt, ObservePtrResetNull) {
  PseudoObservable obs;
  PseudoObservable::ObservedPtr ptr(&obs);
  EXPECT_EQ(1u, obs.ActiveObservedPtrs());
  ptr.Reset();
  EXPECT_EQ(0u, obs.ActiveObservedPtrs());
}

TEST(fxcrt, ObservePtrReset) {
  PseudoObservable obs1;
  PseudoObservable obs2;
  PseudoObservable::ObservedPtr ptr(&obs1);
  EXPECT_EQ(1u, obs1.ActiveObservedPtrs());
  EXPECT_EQ(0u, obs2.ActiveObservedPtrs());
  ptr.Reset(&obs2);
  EXPECT_EQ(0u, obs1.ActiveObservedPtrs());
  EXPECT_EQ(1u, obs2.ActiveObservedPtrs());
}

TEST(fxcrt, ObservePtrEquals) {
  PseudoObservable obj1;
  PseudoObservable obj2;
  PseudoObservable::ObservedPtr null_ptr1;
  PseudoObservable::ObservedPtr obj1_ptr1(&obj1);
  PseudoObservable::ObservedPtr obj2_ptr1(&obj2);
  {
    PseudoObservable::ObservedPtr null_ptr2;
    EXPECT_TRUE(null_ptr1 == null_ptr2);

    PseudoObservable::ObservedPtr obj1_ptr2(&obj1);
    EXPECT_TRUE(obj1_ptr1 == obj1_ptr2);

    PseudoObservable::ObservedPtr obj2_ptr2(&obj2);
    EXPECT_TRUE(obj2_ptr1 == obj2_ptr2);
  }
  EXPECT_FALSE(null_ptr1 == obj1_ptr1);
  EXPECT_FALSE(null_ptr1 == obj2_ptr1);
  EXPECT_FALSE(obj1_ptr1 == obj2_ptr1);
}

TEST(fxcrt, ObservePtrNotEquals) {
  PseudoObservable obj1;
  PseudoObservable obj2;
  PseudoObservable::ObservedPtr null_ptr1;
  PseudoObservable::ObservedPtr obj1_ptr1(&obj1);
  PseudoObservable::ObservedPtr obj2_ptr1(&obj2);
  {
    PseudoObservable::ObservedPtr null_ptr2;
    PseudoObservable::ObservedPtr obj1_ptr2(&obj1);
    PseudoObservable::ObservedPtr obj2_ptr2(&obj2);
    EXPECT_FALSE(null_ptr1 != null_ptr2);
    EXPECT_FALSE(obj1_ptr1 != obj1_ptr2);
    EXPECT_FALSE(obj2_ptr1 != obj2_ptr2);
  }
  EXPECT_TRUE(null_ptr1 != obj1_ptr1);
  EXPECT_TRUE(null_ptr1 != obj2_ptr1);
  EXPECT_TRUE(obj1_ptr1 != obj2_ptr1);
}

TEST(fxcrt, ObservePtrBool) {
  PseudoObservable obj1;
  PseudoObservable::ObservedPtr null_ptr;
  PseudoObservable::ObservedPtr obj1_ptr(&obj1);
  bool null_bool = !!null_ptr;
  bool obj1_bool = !!obj1_ptr;
  EXPECT_FALSE(null_bool);
  EXPECT_TRUE(obj1_bool);
}

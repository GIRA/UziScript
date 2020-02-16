(ns plugin.compiler-test
  (:require [clojure.test :refer :all]
            [plugin.compiler.ast-utils :as ast-utils]
            [plugin.compiler.core :as compiler :refer [compile-tree]])
  (:use [plugin.test-utils]))

(deftest empty-program-test
  (let [ast {:__class__ "UziProgramNode",
             :scripts [{:__class__ "UziTaskNode",
                        :name "empty",
                        :arguments [],
                        :state "running",
                        :tickingRate {:__class__ "UziTickingRateNode",
                                      :value 1,
                                      :scale "s"},
                        :body {:__class__ "UziBlockNode",
                               :statements []}}]}
        expected {:__class__ "UziProgram",
                  :scripts [{:__class__ "UziScript",
                             :arguments [],
                             :delay {:__class__ "UziVariable",
                                     :value 1000},
                             :instructions [],
                             :locals [],
                             :name "empty",
                             :ticking true}],
                  :variables #{{:__class__ "UziVariable",
                                :value 1000}}}
        actual (compile-tree ast)]
    (is (equivalent? expected actual))))


(deftest program-with-global-variable-test
  (let [ast {:__class__ "UziProgramNode",
             :globals [{:__class__ "UziVariableDeclarationNode",
                        :name "counter"}],
             :scripts [{:__class__ "UziTaskNode",
                        :name "empty",
                        :arguments [],
                        :state "running",
                        :tickingRate {:__class__ "UziTickingRateNode",
                                      :value 1,
                                      :scale "s"},
                        :body {:__class__ "UziBlockNode",
                               :statements [{:__class__ "UziAssignmentNode",
                                             :left {:__class__ "UziVariableNode",
                                                    :name "counter"},
                                             :right {:__class__ "UziCallNode",
                                                     :selector "+",
                                                     :arguments [{:__class__ "Association",
                                                                  :value {:__class__ "UziVariableNode",
                                                                          :name "counter"}},
                                                                 {:__class__ "Association",
                                                                  :value {:__class__ "UziNumberLiteralNode",
                                                                          :value 1}}]}}]}}],
             :primitives []}
        expected {:__class__ "UziProgram",
                  :scripts [{:__class__ "UziScript",
                             :arguments [],
                             :delay {:__class__ "UziVariable",
                                     :value 1000},
                             :instructions [{:__class__ "UziPushInstruction",
                                             :argument {:__class__ "UziVariable",
                                                        :name "counter",
                                                        ;:value 0
                                                        }},
                                            {:__class__ "UziPushInstruction",
                                             :argument {:__class__ "UziVariable",
                                                        :value 1}},
                                            {:__class__ "UziPrimitiveCallInstruction",
                                             :argument {:__class__ "UziPrimitive",
                                                        ;:code 6,
                                                        :name "add",
                                                        ;:stackTransition {:__class__ "Association",
                                                        ;                  :key 2,
                                                        ;                  :value 1}
                                                        }},
                                            {:__class__ "UziPopInstruction",
                                             :argument {:__class__ "UziVariable",
                                                        :name "counter",
                                                        ;:value 0
                                                        }}],
                             :locals [],
                             :name "empty",
                             :ticking true}],
                  :variables #{{:__class__ "UziVariable",
                                :name "counter",
                                :value 0},
                               {:__class__ "UziVariable",
                                :value 1000},
                               {:__class__ "UziVariable",
                                :value 1}}}
        actual (compile-tree ast)]
    (is (equivalent? expected actual))))

(deftest task-without-ticking-rate
  (let [ast {:__class__ "UziProgramNode",
             :scripts [{:__class__ "UziTaskNode",,
                        :name "foo",
                        :arguments [],
                        :state "running",
                        :body {:__class__ "UziBlockNode",
                               :statements []}}],
             :primitives []}
        expected {:__class__ "UziProgram",
                  :scripts [{:__class__ "UziScript",
                             :arguments [],
                             :delay {:__class__ "UziVariable",
                                     :value 0},
                             :instructions [],
                             :locals [],
                             :name "foo",
                             :ticking true}],
                  :variables #{{:__class__ "UziVariable",
                               :value 0}}}
        actual (compile-tree ast)]
    (is (equivalent? expected actual))))

(deftest task-with-once
  (let [ast {:__class__ "UziProgramNode",
             :scripts [{:__class__ "UziTaskNode",,
                        :name "foo",
                        :arguments [],
                        :state "once",
                        :body {:__class__ "UziBlockNode",
                               :statements []}}],
             :primitives []}
        expected {:__class__ "UziProgram",
                  :scripts [{:__class__ "UziScript",
                             :arguments [],
                             :delay {:__class__ "UziVariable",
                                     :value 0},
                             :instructions [{:__class__ "UziStopScriptInstruction",
                                             :argument "foo"}],
                             :locals [],
                             :name "foo",
                             :ticking true}],
                  :variables #{{:__class__ "UziVariable",
                                :value 0}}}
        actual (compile-tree ast)]
    (is (equivalent? expected actual))))

(deftest program-with-local-variable
  (let [ast {:__class__ "UziProgramNode",
             :scripts [{:__class__ "UziTaskNode",
                        :name "foo",
                        :arguments [],
                        :state "once",
                        :tickingRate nil,
                        :body {:__class__ "UziBlockNode",
                               :statements [{:__class__ "UziVariableDeclarationNode",
                                             :name "pin",
                                             :value {:__class__ "UziPinLiteralNode",
                                                     :type "D",
                                                     :number 13}},
                                            {:__class__ "UziCallNode",
                                             :selector "toggle",
                                             :arguments [{:__class__ "Association",
                                                          :key nil,
                                                          :value {:__class__ "UziVariableNode",
                                                                  :name "pin"}}]}]}}]}
        expected {:__class__ "UziProgram",
                  :scripts [{:__class__ "UziScript",
                             :name "foo",
                             :ticking true,
                             :delay {:__class__ "UziVariable",
                                     :value 0},
                             :locals [{:__class__ "UziVariable",
                                       :name "pin#1",
                                       :value 0}],
                             :instructions [{:__class__ "UziPushInstruction",
                                             :argument {:__class__ "UziVariable",
                                                        :value 13}},
                                            {:__class__ "UziWriteLocalInstruction",
                                             :argument {:__class__ "UziVariable",
                                                        :name "pin#1",}},
                                            {:__class__ "UziReadLocalInstruction",
                                             :argument {:__class__ "UziVariable",
                                                        :name "pin#1"}},
                                            {:__class__ "UziPrimitiveCallInstruction",
                                             :argument {:__class__ "UziPrimitive",
                                                        ;:code 2,
                                                        :name "toggle",
                                                        ;:stackTransition {:__class__ "Association",
                                                        ;                  :key 1,
                                                        ;                  :value 0}
                                                        }},
                                            {:__class__ "UziStopScriptInstruction",
                                             :argument "foo"}]}],
                  :variables #{{:__class__ "UziVariable",
                                :value 0},
                               {:__class__ "UziVariable",
                                :value 13}}}
        actual (compile-tree ast)]
    (is (equivalent? expected actual))))


(deftest ast-transform-test
  (let [original {:__class__ "UziProgramNode",
                  :scripts [{:__class__ "UziTaskNode",
                             :name "empty",
                             :arguments [],
                             :state "running",
                             :tickingRate {:__class__ "UziTickingRateNode",
                                           :value 1,
                                           :scale "s"},
                             :body {:__class__ "UziBlockNode",
                                    :statements [{:__class__ "UziCallNode",
                                                  :selector "toggle",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :value 13}}]},
                                                 {:__class__ "UziCallNode",
                                                  :selector "turnOn",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :value 12}}]}]}}]}
        expected {:__class__ "UziProgramNode",
                  :__foo__ 1,
                  :scripts [{:__class__ "UziTaskNode",
                             :__foo__ 2,
                             :name "empty",
                             :arguments [],
                             :state "running",
                             :tickingRate {:__class__ "UziTickingRateNode",
                                           :__bar__ 5,
                                           :value 1,
                                           :scale "s"},
                             :body {:__class__ "UziBlockNode",
                                    :__bar__ 5,
                                    :statements [{:__class__ "UziCallNode",
                                                  :__foo__ 3,
                                                  :selector "toggle",
                                                  :arguments [{:__class__ "Association",
                                                               :__bar__ 5,
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :__foo__ 4
                                                                       :value 13}}]},
                                                 {:__class__ "UziCallNode",
                                                  :__foo__ 3,
                                                  :selector "turnOn",
                                                  :arguments [{:__class__ "Association",
                                                               :__bar__ 5,
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :__foo__ 4,
                                                                       :value 12}}]}]}}]}
        actual (ast-utils/transform
                original
                "UziProgramNode" #(assoc % :__foo__ 1)
                "UziTaskNode" #(assoc % :__foo__ 2)
                "UziCallNode" #(assoc % :__foo__ 3)
                "UziNumberLiteralNode" #(assoc % :__foo__ 4)
                :default #(assoc % :__bar__ 5))]
    (is (= expected actual))))

(deftest ast-transform-without-default-clause
  (let [original {:__class__ "UziProgramNode",
                  :scripts [{:__class__ "UziTaskNode",
                             :name "empty",
                             :arguments [],
                             :state "running",
                             :tickingRate {:__class__ "UziTickingRateNode",
                                           :value 1,
                                           :scale "s"},
                             :body {:__class__ "UziBlockNode",
                                    :statements [{:__class__ "UziCallNode",
                                                  :selector "toggle",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :value 13}}]},
                                                 {:__class__ "UziCallNode",
                                                  :selector "turnOn",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :value 12}}]}]}}]}
        expected {:__class__ "UziProgramNode",
                  :__foo__ 1,
                  :scripts [{:__class__ "UziTaskNode",
                             :__foo__ 2,
                             :name "empty",
                             :arguments [],
                             :state "running",
                             :tickingRate {:__class__ "UziTickingRateNode",
                                           :value 1,
                                           :scale "s"},
                             :body {:__class__ "UziBlockNode",
                                    :statements [{:__class__ "UziCallNode",
                                                  :__foo__ 3,
                                                  :selector "toggle",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :__foo__ 4
                                                                       :value 13}}]},
                                                 {:__class__ "UziCallNode",
                                                  :__foo__ 3,
                                                  :selector "turnOn",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :__foo__ 4,
                                                                       :value 12}}]}]}}]}
        actual (ast-utils/transform
                original
                "UziProgramNode" #(assoc % :__foo__ 1)
                "UziTaskNode" #(assoc % :__foo__ 2)
                "UziCallNode" #(assoc % :__foo__ 3)
                "UziNumberLiteralNode" #(assoc % :__foo__ 4))]
    (is (= expected actual))))

(deftest ast-transform-pred-test
  (let [original {:__class__ "UziProgramNode",
                  :scripts [{:__class__ "UziTaskNode",
                             :name "empty",
                             :arguments [],
                             :state "running",
                             :tickingRate {:__class__ "UziTickingRateNode",
                                           :value 1,
                                           :scale "s"},
                             :body {:__class__ "UziBlockNode",
                                    :statements [{:__class__ "UziCallNode",
                                                  :selector "toggle",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :value 13}}]},
                                                 {:__class__ "UziCallNode",
                                                  :selector "turnOn",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :value 12}}]}]}}]}
        expected {:__class__ "UziProgramNode",
                  :__foo__ 1,
                  :scripts [{:__class__ "UziTaskNode",
                             :__foo__ 2,
                             :name "empty",
                             :arguments [],
                             :state "running",
                             :tickingRate {:__class__ "UziTickingRateNode",
                                           :__bar__ 5,
                                           :value 1,
                                           :scale "s"},
                             :body {:__class__ "UziBlockNode",
                                    :__bar__ 5,
                                    :statements [{:__class__ "UziCallNode",
                                                  :__foo__ 3,
                                                  :selector "toggle",
                                                  :arguments [{:__class__ "Association",
                                                               :__bar__ 5,
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :__foo__ 4
                                                                       :value 13}}]},
                                                 {:__class__ "UziCallNode",
                                                  :__foo__ 3,
                                                  :selector "turnOn",
                                                  :arguments [{:__class__ "Association",
                                                               :__bar__ 5,
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :__foo__ 4,
                                                                       :value 12}}]}]}}]}
        actual (ast-utils/transform-pred
                original
                #(= "UziProgramNode" (get % :__class__)) #(assoc % :__foo__ 1)
                #(= "UziTaskNode" (get % :__class__)) #(assoc % :__foo__ 2)
                #(= "UziCallNode" (get % :__class__)) #(assoc % :__foo__ 3)
                #(= "UziNumberLiteralNode" (get % :__class__)) #(assoc % :__foo__ 4)
                :default #(assoc % :__bar__ 5))]
    (is (= expected actual))))

(deftest ast-transform-pred-without-default-clause
  (let [original {:__class__ "UziProgramNode",
                  :scripts [{:__class__ "UziTaskNode",
                             :name "empty",
                             :arguments [],
                             :state "running",
                             :tickingRate {:__class__ "UziTickingRateNode",
                                           :value 1,
                                           :scale "s"},
                             :body {:__class__ "UziBlockNode",
                                    :statements [{:__class__ "UziCallNode",
                                                  :selector "toggle",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :value 13}}]},
                                                 {:__class__ "UziCallNode",
                                                  :selector "turnOn",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :value 12}}]}]}}]}
        expected {:__class__ "UziProgramNode",
                  :__foo__ 1,
                  :scripts [{:__class__ "UziTaskNode",
                             :__foo__ 2,
                             :name "empty",
                             :arguments [],
                             :state "running",
                             :tickingRate {:__class__ "UziTickingRateNode",
                                           :value 1,
                                           :scale "s"},
                             :body {:__class__ "UziBlockNode",
                                    :statements [{:__class__ "UziCallNode",
                                                  :__foo__ 3,
                                                  :selector "toggle",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :__foo__ 4
                                                                       :value 13}}]},
                                                 {:__class__ "UziCallNode",
                                                  :__foo__ 3,
                                                  :selector "turnOn",
                                                  :arguments [{:__class__ "Association",
                                                               :key nil,
                                                               :value {:__class__ "UziNumberLiteralNode",
                                                                       :__foo__ 4,
                                                                       :value 12}}]}]}}]}
        actual (ast-utils/transform-pred
                original
                #(= "UziProgramNode" (get % :__class__)) #(assoc % :__foo__ 1)
                #(= "UziTaskNode" (get % :__class__)) #(assoc % :__foo__ 2)
                #(= "UziCallNode" (get % :__class__)) #(assoc % :__foo__ 3)
                #(= "UziNumberLiteralNode" (get % :__class__)) #(assoc % :__foo__ 4))]
    (is (= expected actual))))

(deftest program-with-local-variable-whose-value-is-a-compile-time-constant
  (let [ast {:__class__ "UziProgramNode",
             :imports [],
             :globals [],
             :scripts [{:__class__ "UziTaskNode",
                        :id "vCqKea@l^3heM}9|Cw!s",
                        :name "default",
                        :arguments [],
                        :state "once",
                        :tickingRate nil,
                        :body {:__class__ "UziBlockNode",
                               :id "vCqKea@l^3heM}9|Cw!s",
                               :statements [{:__class__ "UziVariableDeclarationNode",
                                             :id "iAN`05Zsui?}QXL3*fc.",
                                             :name "a",
                                             :value {:__class__ "UziNumberLiteralNode",
                                                     :id "t-BLxzZ!K!}.slTn=GTJ",
                                                     :value 0}},
                                            {:__class__ "UziCallNode",
                                             :id "mCG^q:4a`_BOM?~uQ^M3",
                                             :selector "toggle",
                                             :arguments [{:__class__ "Association",
                                                          :key nil,
                                                          :value {:__class__ "UziVariableNode",
                                                                  :id "GdE:qSgC+$+K]fROtP*!",
                                                                  :name "a"}}]},
                                            {:__class__ "UziVariableDeclarationNode",
                                             :id "S-IR![^~KOGd/U:XM},*",
                                             :name "b",
                                             :value {:__class__ "UziNumberLiteralNode",
                                                     :id "BAihFZribNgx|R+Qzmy0",
                                                     :value 0}},
                                            {:__class__ "UziCallNode",
                                             :id "^6:^K]]q0mPOB`AyfA@=",
                                             :selector "toggle",
                                             :arguments [{:__class__ "Association",
                                                          :key nil,
                                                          :value {:__class__ "UziVariableNode",
                                                                  :id "zKuonYS2%)X_JO48F~OK",
                                                                  :name "b"}}]}]}}],
             :primitives []}
        expected {:__class__ "UziProgram",
                  :scripts [{:__class__ "UziScript",
                             :arguments [],
                             :delay {:__class__ "UziVariable",
                                     :value 0},
                             :instructions [{:__class__ "UziReadLocalInstruction",
                                             :argument {:__class__ "UziVariable",
                                                        :name "a#1"}},
                                            {:__class__ "UziPrimitiveCallInstruction",
                                             :argument {:__class__ "UziPrimitive",
                                                        :name "toggle"}},
                                            {:__class__ "UziReadLocalInstruction",
                                             :argument {:__class__ "UziVariable",
                                                        :name "b#2"}},
                                            {:__class__ "UziPrimitiveCallInstruction",
                                             :argument {:__class__ "UziPrimitive",
                                                        :name "toggle"}},
                                            {:__class__ "UziStopScriptInstruction",
                                             :argument "default"}],
                             :locals [{:__class__ "UziVariable",
                                       :name "a#1",
                                       :value 0},
                                      {:__class__ "UziVariable",
                                       :name "b#2",
                                       :value 0}],
                             :name "default",
                             :ticking true}],
                  :variables #{{:__class__ "UziVariable" :value 0}}}
        actual (compile-tree ast)]
    (is (equivalent? expected actual))))


    (deftest program-with-local-variable-whose-value-is-a-compile-time-constant-different-than-zero
      (let [ast {:__class__ "UziProgramNode",
                 :imports [],
                 :globals [],
                 :scripts [{:__class__ "UziTaskNode",
                            :name "default",
                            :arguments [],
                            :state "once",
                            :tickingRate nil,
                            :body {:__class__ "UziBlockNode",
                                   :statements [{:__class__ "UziVariableDeclarationNode",
                                                 :name "a",
                                                 :value {:__class__ "UziNumberLiteralNode",
                                                         :value 1}},
                                                {:__class__ "UziCallNode",
                                                 :selector "toggle",
                                                 :arguments [{:__class__ "Association",
                                                              :key nil,
                                                              :value {:__class__ "UziVariableNode",
                                                                      :name "a"}}]},
                                                {:__class__ "UziVariableDeclarationNode",
                                                 :name "b",
                                                 :value {:__class__ "UziNumberLiteralNode",
                                                         :value 2}},
                                                {:__class__ "UziCallNode",
                                                 :selector "toggle",
                                                 :arguments [{:__class__ "Association",
                                                              :key nil,
                                                              :value {:__class__ "UziVariableNode",
                                                                      :name "b"}}]}]}}],
                 :primitives []}
            expected {:__class__ "UziProgram",
                      :scripts [{:__class__ "UziScript",
                                 :arguments [],
                                 :delay {:__class__ "UziVariable",
                                         :value 0},
                                 :instructions [{:__class__ "UziReadLocalInstruction",
                                                 :argument {:__class__ "UziVariable",
                                                            :name "a#1"}},
                                                {:__class__ "UziPrimitiveCallInstruction",
                                                 :argument {:__class__ "UziPrimitive",
                                                            :name "toggle"}},
                                                {:__class__ "UziReadLocalInstruction",
                                                 :argument {:__class__ "UziVariable",
                                                            :name "b#2"}},
                                                {:__class__ "UziPrimitiveCallInstruction",
                                                 :argument {:__class__ "UziPrimitive",
                                                            :name "toggle",}},
                                                {:__class__ "UziStopScriptInstruction",
                                                 :argument "default"}],
                                 :locals [{:__class__ "UziVariable",
                                           :name "a#1",
                                           :value 1},
                                          {:__class__ "UziVariable",
                                           :name "b#2",
                                           :value 2}],
                                 :name "default",
                                 :ticking true}],
                      :variables #{{:__class__ "UziVariable", :value 0},
                                  {:__class__ "UziVariable", :value 1},
                                  {:__class__ "UziVariable", :value 2}}}
            actual (compile-tree ast)]
        (is (equivalent? expected actual))))

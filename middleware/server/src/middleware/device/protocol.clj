(ns middleware.device.protocol
  (:require [clojure.string :as str]))

; Version number
(def MAJOR_VERSION 0)
(def MINOR_VERSION 8)

; Outgoing
(def MSG_OUT_CONNECTION_REQUEST 255)
(def MSG_OUT_SET_PROGRAM 0)
(def MSG_OUT_SET_VALUE 1)
(def MSG_OUT_SET_MODE 2)
(def MSG_OUT_START_REPORTING 3)
(def MSG_OUT_STOP_REPORTING 4)
(def MSG_OUT_SET_REPORT 5)
(def MSG_OUT_SAVE_PROGRAM 6)
(def MSG_OUT_KEEP_ALIVE 7)
(def MSG_OUT_PROFILE 8)
(def MSG_OUT_SET_REPORT_INTERVAL 9)
(def MSG_OUT_SET_GLOBAL 10)
(def MSG_OUT_SET_GLOBAL_REPORT 11)
(def MSG_OUT_DEBUG_CONTINUE	12)
(def MSG_OUT_DEBUG_SET_BREAKPOINTS 13)
(def MSG_OUT_DEBUG_SET_BREAKPOINTS_ALL 14)

; Incoming
(def MSG_IN_ERROR 0)
(def MSG_IN_PIN_VALUE 1)
(def MSG_IN_PROFILE 2)
(def MSG_IN_GLOBAL_VALUE 3)
(def MSG_IN_TRACE 4)
(def MSG_IN_COROUTINE_STATE 5)
(def MSG_IN_RUNNING_SCRIPTS 6)
(def MSG_IN_FREE_RAM 7)
(def MSG_IN_SERIAL_TUNNEL 8)

; Error messages
(def ^:private error-msgs
  [0 "NO_ERROR"
   1 "STACK_OVERFLOW"
   2 "STACK_UNDERFLOW"
   4 "ACCESS_VIOLATION"
   8 "OUT_OF_MEMORY"
   16 "READER_TIMEOUT"
   32 "DISCONNECT_ERROR"
   64 "READER_CHECKSUM_FAIL"])

(defn error-msg [^long code]
  (if (= 0 code)
    "NO_ERROR"
    (let [msg (str/join
               " & "
               (map (fn [[_ k]] k)
                    (filter (fn [[^long c _]] (not= 0 (bit-and code c)))
                            (partition-all 2 error-msgs))))]
      (if (empty? msg)
        (str "UNKNOWN_ERROR (" code ")")
        msg))))

(defn error? [code] (not= 0 code))

(defn error-disconnect? [^long code]
  (not= 0 (bit-and code 32)))

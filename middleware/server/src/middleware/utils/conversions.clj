(ns middleware.utils.conversions)


(defn bytes->uint32 [[^byte n1 ^byte n2 ^byte n3 ^byte n4]]
  (bit-or (bit-shift-left n1 24)
          (bit-shift-left n2 16)
          (bit-shift-left n3 8)
          n4))

(defn uint32->float [^long uint32]
  (Float/intBitsToFloat (unchecked-int uint32)))

(defn bytes->float [^bytes bytes]
  (uint32->float (bytes->uint32 bytes)))

(defn bytes->uint16 [[^byte msb ^byte lsb]]
  (bit-or (bit-shift-left msb 8)
          lsb))

(defn float->uint32 [^double float]
  (Float/floatToRawIntBits (unchecked-float float)))

(defn two's-complement [^long byte]
  (if (>= byte 0)
    byte
    (bit-and 16rFF
             (inc (bit-xor (Math/abs byte)
                           16rFF)))))

(defn non-fraction [value]
  (if (ratio? value)
    (float value)
    value))

(defn- NaN? [^double value]
  (not (== value value)))

(defn try-integer [value]
  (if (NaN? value)
    value
    (let [int-val (unchecked-int value)]
      (if (= 0 (compare int-val value))
        int-val
        value))))

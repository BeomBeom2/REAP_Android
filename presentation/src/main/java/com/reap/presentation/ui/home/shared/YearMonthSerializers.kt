package com.reap.presentation.ui.home.shared


/**
 * A serializer for [YearMonth] that uses the ISO 8601 representation.
 *
 * JSON example: `"2020-01"`
 *
 * @see YearMonth.toString
 */
public object YearMonthIso8601Serializer : KSerializer<YearMonth> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.kizitonwose.calendar.core.YearMonth", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): YearMonth =
        decoder.decodeString().fromIso8601YearMonth()

    override fun serialize(encoder: Encoder, value: YearMonth) {
        encoder.encodeString(value.toIso8601String())
    }
}

/**
 * A serializer for [YearMonth] that represents a value as its components.
 *
 * JSON example: `{"year":2020,"month":12}`
 */
public object YearMonthComponentSerializer : KSerializer<YearMonth> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("com.kizitonwose.calendar.core.YearMonth") {
            element<Int>("year")
            element<Short>("month")
        }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): YearMonth =
        decoder.decodeStructure(descriptor) {
            var year: Int? = null
            var month: Short? = null
            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> year = decodeIntElement(descriptor, 0)
                    1 -> month = decodeShortElement(descriptor, 1)
                    CompositeDecoder.DECODE_DONE -> break@loop // https://youtrack.jetbrains.com/issue/KT-42262
                    else -> throw SerializationException("Unexpected index: $index")
                }
            }
            if (year == null) throw MissingFieldException(missingField = "year", serialName = descriptor.serialName)
            if (month == null) throw MissingFieldException(missingField = "month", serialName = descriptor.serialName)
            YearMonth(year, month.toInt())
        }

    override fun serialize(encoder: Encoder, value: YearMonth) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.year)
            encodeShortElement(descriptor, 1, value.monthNumber.toShort())
        }
    }
}

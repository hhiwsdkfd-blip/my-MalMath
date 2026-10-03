package com.example.math

data class UnitCategory(
    val id: String,
    val nameArabic: String,
    val units: List<UnitItem>
)

data class UnitItem(
    val id: String,
    val nameArabic: String,
    val symbol: String,
    val factorToBase: Double // Multiply by this to get base unit
)

object UnitConverter {
    val categories: List<UnitCategory> = listOf(
        UnitCategory(
            id = "length",
            nameArabic = "الطول والمساحة",
            units = listOf(
                UnitItem("m", "متر", "m", 1.0),
                UnitItem("km", "كيلومتر", "km", 1000.0),
                UnitItem("cm", "سنتيمتر", "cm", 0.01),
                UnitItem("mm", "ميليمتر", "mm", 0.001),
                UnitItem("in", "بوصة (إنش)", "in", 0.0254),
                UnitItem("ft", "قدم", "ft", 0.3048),
                UnitItem("yd", "ياردة", "yd", 0.9144),
                UnitItem("mi", "ميل", "mi", 1609.344)
            )
        ),
        UnitCategory(
            id = "mass",
            nameArabic = "الكتلة والوزن",
            units = listOf(
                UnitItem("kg", "كيلوغرام", "kg", 1.0),
                UnitItem("g", "غرام", "g", 0.001),
                UnitItem("mg", "ميليغرام", "mg", 0.000001),
                UnitItem("t", "طن متري", "t", 1000.0),
                UnitItem("lb", "رطل (باوند)", "lb", 0.45359237),
                UnitItem("oz", "أونصة", "oz", 0.028349523)
            )
        ),
        UnitCategory(
            id = "speed",
            nameArabic = "السرعة",
            units = listOf(
                UnitItem("m_s", "متر/ثانية", "m/s", 1.0),
                UnitItem("km_h", "كم/ساعة", "km/h", 1.0 / 3.6),
                UnitItem("mph", "ميل/ساعة", "mph", 0.44704),
                UnitItem("knot", "عقدة بحرية", "kn", 0.514444)
            )
        ),
        UnitCategory(
            id = "pressure",
            nameArabic = "الضغط",
            units = listOf(
                UnitItem("pa", "باسكال", "Pa", 1.0),
                UnitItem("kpa", "كيلوباسكال", "kPa", 1000.0),
                UnitItem("bar", "بار", "bar", 100000.0),
                UnitItem("atm", "ضغط جوي قياسي", "atm", 101325.0),
                UnitItem("psi", "رطل/بوصة مربعة", "psi", 6894.757),
                UnitItem("mmhg", "ميليمتر زئبق (Torr)", "mmHg", 133.322)
            )
        ),
        UnitCategory(
            id = "energy",
            nameArabic = "الطاقة والعمل",
            units = listOf(
                UnitItem("j", "جول", "J", 1.0),
                UnitItem("kj", "كيلوجول", "kJ", 1000.0),
                UnitItem("cal", "سعرة حرارية", "cal", 4.184),
                UnitItem("kcal", "كيلوسعرة", "kcal", 4184.0),
                UnitItem("kwh", "كيلوواط ساعة", "kWh", 3600000.0),
                UnitItem("ev", "إلكترون فولت", "eV", 1.602176634e-19)
            )
        )
    )

    fun convert(value: Double, fromUnit: UnitItem, toUnit: UnitItem): Double {
        val baseValue = value * fromUnit.factorToBase
        return baseValue / toUnit.factorToBase
    }

    fun convertTemperature(value: Double, fromUnit: String, toUnit: String): Double {
        val celsius = when (fromUnit) {
            "C" -> value
            "F" -> (value - 32.0) * (5.0 / 9.0)
            "K" -> value - 273.15
            else -> value
        }
        return when (toUnit) {
            "C" -> celsius
            "F" -> (celsius * 9.0 / 5.0) + 32.0
            "K" -> celsius + 273.15
            else -> celsius
        }
    }
}

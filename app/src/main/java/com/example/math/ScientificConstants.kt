package com.example.math

data class ScientificConstant(
    val symbol: String,
    val nameArabic: String,
    val nameEnglish: String,
    val value: Double,
    val unit: String,
    val category: String
)

object ScientificConstants {
    val list: List<ScientificConstant> = listOf(
        // Universal
        ScientificConstant("c", "سرعة الضوء في الفراغ", "Speed of light in vacuum", 299792458.0, "m/s", "ثوابت كونية"),
        ScientificConstant("h", "ثابت بلانك", "Planck constant", 6.62607015e-34, "J·s", "ثوابت كونية"),
        ScientificConstant("ℏ", "ثابت ديراك (بلانك المخفض)", "Reduced Planck constant", 1.054571817e-34, "J·s", "ثوابت كونية"),
        ScientificConstant("G", "ثابت الجاذبية العام", "Newtonian gravitational constant", 6.67430e-11, "m³/(kg·s²)", "ثوابت كونية"),
        ScientificConstant("g", "تسارع الجاذبية الأرضية القياسي", "Standard gravity", 9.80665, "m/s²", "ثوابت كونية"),

        // Electromagnetic
        ScientificConstant("e", "شحنة الإلكترون الأساسية", "Elementary charge", 1.602176634e-19, "C", "كهرومغناطيسية"),
        ScientificConstant("ε₀", "سماحية الفراغ الكهربائية", "Vacuum electric permittivity", 8.8541878128e-12, "F/m", "كهرومغناطيسية"),
        ScientificConstant("μ₀", "نفاذية الفراغ المغناطيسية", "Vacuum magnetic permeability", 1.25663706212e-6, "N/A²", "كهرومغناطيسية"),
        ScientificConstant("Z₀", "المعاوقة المميزة للفراغ", "Characteristic impedance of vacuum", 376.730313668, "Ω", "كهرومغناطيسية"),

        // Atomic & Nuclear
        ScientificConstant("m_e", "كتلة الإلكترون", "Electron mass", 9.1093837015e-31, "kg", "ذرية ونووية"),
        ScientificConstant("m_p", "كتلة البروتون", "Proton mass", 1.67262192369e-27, "kg", "ذرية ونووية"),
        ScientificConstant("m_n", "كتلة النيوترون", "Neutron mass", 1.67492749804e-27, "kg", "ذرية ونووية"),
        ScientificConstant("u", "وحدة الكتل الذرية", "Atomic mass unit", 1.66053906660e-27, "kg", "ذرية ونووية"),
        ScientificConstant("a₀", "نصف قطر بور", "Bohr radius", 5.29177210903e-11, "m", "ذرية ونووية"),
        ScientificConstant("R_∞", "ثابت ريدبيرغ", "Rydberg constant", 10973731.568160, "m⁻¹", "ذرية ونووية"),

        // Physico-Chemical
        ScientificConstant("N_A", "عدد أفوجادرو", "Avogadro constant", 6.02214076e23, "mol⁻¹", "فيزيوكيميائية"),
        ScientificConstant("k_B", "ثابت بولتزمان", "Boltzmann constant", 1.380649e-23, "J/K", "فيزيوكيميائية"),
        ScientificConstant("R", "ثابت الغازات العام", "Molar gas constant", 8.314462618, "J/(mol·K)", "فيزيوكيميائية"),
        ScientificConstant("σ", "ثابت ستيفان-بولتزمان", "Stefan-Boltzmann constant", 5.670374419e-8, "W/(m²·K⁴)", "فيزيوكيميائية"),
        ScientificConstant("F", "ثابت فاراداي", "Faraday constant", 96485.33212, "C/mol", "فيزيوكيميائية"),

        // Mathematical
        ScientificConstant("π", "النسبة التقريبية ط (باي)", "Pi", Math.PI, "", "رياضيات"),
        ScientificConstant("e", "العدد النيبيري (أساس اللوغاريتم الطبيعي)", "Euler's number", Math.E, "", "رياضيات"),
        ScientificConstant("φ", "النسبة الذهبية (فاي)", "Golden ratio", 1.618033988749895, "", "رياضيات"),
        ScientificConstant("√2", "جذر 2 (ثابت فيثاغورس)", "Square root of 2", 1.414213562373095, "", "رياضيات"),
        ScientificConstant("√3", "جذر 3 (ثابت ثيودوروس)", "Square root of 3", 1.732050807568877, "", "رياضيات"),
        ScientificConstant("ln(2)", "اللوغاريتم الطبيعي لـ 2", "Natural log of 2", 0.6931471805599453, "", "رياضيات"),
        ScientificConstant("ln(10)", "اللوغاريتم الطبيعي لـ 10", "Natural log of 10", 2.302585092994046, "", "رياضيات")
    )
}

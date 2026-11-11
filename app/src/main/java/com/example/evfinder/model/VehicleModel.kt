package com.example.evfinder.model

data class VehicleModel(
    val id: String,
    val brand: String,
    val model: String,
    val batteryCapacityKwh: Double,
    val maxChargePowerKw: Int,
    val supportedConnectors: List<ConnectorType>,
    val imageUrl: String = ""
) {
    val displayName: String
        get() = "$brand $model ($maxChargePowerKw kW)"

    companion object {
        val sampleVehicles = listOf(
            VehicleModel(
                id = "veh_01",
                brand = "Peugeot",
                model = "e-208",
                batteryCapacityKwh = 50.0,
                maxChargePowerKw = 100,
                supportedConnectors = listOf(ConnectorType.CCS_2, ConnectorType.TYPE_2)
            ),
            VehicleModel(
                id = "veh_02",
                brand = "Nissan",
                model = "Leaf",
                batteryCapacityKwh = 40.0,
                maxChargePowerKw = 50,
                supportedConnectors = listOf(ConnectorType.CHADEMO, ConnectorType.TYPE_2)
            ),
            VehicleModel(
                id = "veh_03",
                brand = "Coradir",
                model = "Tito EV",
                batteryCapacityKwh = 8.0,
                maxChargePowerKw = 3,
                supportedConnectors = listOf(ConnectorType.TYPE_2)
            ),
            VehicleModel(
                id = "veh_04",
                brand = "Tesla",
                model = "Model 3",
                batteryCapacityKwh = 75.0,
                maxChargePowerKw = 250,
                supportedConnectors = listOf(ConnectorType.TESLA, ConnectorType.CCS_2, ConnectorType.TYPE_2)
            ),
            VehicleModel(
                id = "veh_05",
                brand = "Audi",
                model = "e-tron GT",
                batteryCapacityKwh = 93.0,
                maxChargePowerKw = 270,
                supportedConnectors = listOf(ConnectorType.CCS_2, ConnectorType.TYPE_2)
            ),
            VehicleModel(
                id = "veh_06",
                brand = "Renault",
                model = "Kwid E-Tech",
                batteryCapacityKwh = 26.8,
                maxChargePowerKw = 30,
                supportedConnectors = listOf(ConnectorType.CCS_2, ConnectorType.TYPE_2)
            )
        )
    }
}

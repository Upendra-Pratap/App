
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class VehicleTypeResponse:Serializable {
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("msg")
    @Expose
    var msg: String? = null

    @SerializedName("data")
    @Expose
    var data: List<VehicleTypeData>? = null

    inner class VehicleTypeData:Serializable{

        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("shipmentname")
        @Expose
        var shipmentname: String? = null

        @SerializedName("capabilityname")
        @Expose
        var capabilityname: String? = null

        @SerializedName("vehiclename")
        @Expose
        var vehiclename: String? = null

        @SerializedName("vehicleimage")
        @Expose
        var vehicleimage: String? = null

        @SerializedName("vehicledescription")
        @Expose
        var vehicledescription: String? = null

        @SerializedName("vehiclesize")
        @Expose
        var vehiclesize: String? = null

        @SerializedName("vehicleweight")
        @Expose
        var vehicleweight: Float? = null

        @SerializedName("vehicledistance")
        @Expose
        var vehicledistance: Int? = null

        @SerializedName("vehiclefixcharge")
        @Expose
        var vehiclefixcharge: Float? = null

        @SerializedName("vehicleperkmcharges")
        @Expose
        var vehicleperkmcharges: Float? = null

        @SerializedName("upfront_payment")
        @Expose
        var upfrontPayment: Float? = null

    }
}
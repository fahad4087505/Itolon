
package com.example.myapplication.model.albumdetailmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AlbumDetailModel  implements Serializable {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private AlbumDetailResult result;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public AlbumDetailResult getResult() {
        return result;
    }

    public void setResult(AlbumDetailResult result) {
        this.result = result;
    }

}

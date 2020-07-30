
package com.example.myapplication.model.addcommentsmodel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddCommentModel {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private List<Result> result = null;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

}

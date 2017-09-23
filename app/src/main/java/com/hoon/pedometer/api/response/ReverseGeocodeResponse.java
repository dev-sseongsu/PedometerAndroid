package com.hoon.pedometer.api.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hoon.pedometer.api.MapApi;

/**
 * {@link MapApi#reverseGeocode(String)} 응답 값을 저장하기 위한 클래스.
 * <p>
 * 편의를 위해서 필요한 필드만 선언
 */
public class ReverseGeocodeResponse {

    @SerializedName("result")
    private ReverseGeocodeResult result;

    @Nullable
    public String getAddress() {
        if (result != null) {
            if (result.items != null && result.items.length > 0) {
                AddressDetail detail = result.items[0].addressDetail;
                if (detail != null) {
                    return detail.toString();
                }
            }
        }
        return null;
    }

    private static class ReverseGeocodeResult {
        @SerializedName("items")
        private ReverseGeocodeResultItem[] items;
    }

    private static class ReverseGeocodeResultItem {
        @SerializedName("addrdetail")
        private AddressDetail addressDetail;
    }

    private static class AddressDetail {
        @SerializedName("sido")
        private String sido;
        @SerializedName("sigugun")
        private String sigugun;
        @SerializedName("dongmyun")
        private String dongmyun;
        @SerializedName("ri")
        private String ri;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            appendAddressDetail(sb, sido);
            appendAddressDetail(sb, sigugun);
            appendAddressDetail(sb, dongmyun);
            appendAddressDetail(sb, ri);
            return sb.toString();
        }

        private void appendAddressDetail(@NonNull StringBuilder sb, @NonNull String part) {
            if (!TextUtils.isEmpty(part)) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(part);
            }
        }
    }

}

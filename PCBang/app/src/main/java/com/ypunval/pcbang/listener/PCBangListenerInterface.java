package com.ypunval.pcbang.listener;

import com.ypunval.pcbang.model.Convenience;
import com.ypunval.pcbang.model.Doe;
import com.ypunval.pcbang.model.Line;
import com.ypunval.pcbang.model.PCBang;
import com.ypunval.pcbang.model.Review;
import com.ypunval.pcbang.model.Si;
import com.ypunval.pcbang.model.Subway;

/**
 * Created by uncheon on 16. 4. 12..
 */
public class PCBangListenerInterface {

    public interface OnDoeClickListener{
        void onDoeClick(Doe doe);
    }

    public interface OnSiClickListener{
        void onSiClick(Si si);
    }

    public interface OnLineClickListener{
        void onLineClick(Line line);
    }

    public interface OnSubwayClickListener{
        void onSubwayClick(Subway subway);
    }

    public interface OnNearByClickListener{
        void onNearByHeaderAddConvenience(Convenience convenience);
        void onNearByHeaderRemoveConvenience(Convenience convenience);
        void onNearByItemClick(PCBang pcBang);
        void onNearByFooterClick();
    }

    public interface OnLocationChangeListener{
        void onLocationChange(double latitude, double longitude);
    }

    public interface OnReviewClickListener {
        void onReviewClick(Review review);
        void onReviewWriteClick();
    }


    public interface  OnFinishRegisterListener {
        void onSuccessRegister(String responseString);
        void onFailureNetwork();
    }


    public interface OnUpdateListener {
        void onSuccessUpdate(String responseString);
        void onFailureNetwork();
    }

    public interface OnNicknameListener {
        void onSuccessNickname(String responseString);
        void onFailureNetwork();
    }
}

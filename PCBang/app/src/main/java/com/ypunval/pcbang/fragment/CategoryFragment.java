package com.ypunval.pcbang.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ypunval.pcbang.R;
import com.ypunval.pcbang.activity.ResultActivity;
import com.ypunval.pcbang.adapter.DoeRVA;
import com.ypunval.pcbang.adapter.LineRVA;
import com.ypunval.pcbang.adapter.SiRVA;
import com.ypunval.pcbang.adapter.SubwayRVA;
import com.ypunval.pcbang.listener.PCBangListenerInterface;
import com.ypunval.pcbang.model.Doe;
import com.ypunval.pcbang.model.Line;
import com.ypunval.pcbang.model.Region;
import com.ypunval.pcbang.model.Si;
import com.ypunval.pcbang.model.Subway;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;

public class CategoryFragment extends BaseRealmFragment {

    @Bind(R.id.tv_tab_location)
    TextView tv_tab_location;
    @Bind(R.id.tv_tab_subway)
    TextView tv_tab_subway;


    @Bind(R.id.ll_location)
    LinearLayout ll_location;
    @Bind(R.id.ll_subway)
    LinearLayout ll_subway;

    @Bind(R.id.rv_line)
    RecyclerView rv_line;
    @Bind(R.id.rv_doe)
    RecyclerView rv_doe;

    @Bind(R.id.rv_si)
    RecyclerView rv_si;
    @Bind(R.id.rv_subway)
    RecyclerView rv_subway;


    @Bind(R.id.tv_subway_capital)
    TextView tv_subway_capital;
    @Bind(R.id.tv_subway_busan)
    TextView tv_subway_busan;
    @Bind(R.id.tv_subway_daegue)
    TextView tv_subway_daegue;
    @Bind(R.id.tv_subway_gj)
    TextView tv_subway_gj;
    @Bind(R.id.tv_subway_dj)
    TextView tv_subway_dj;

    @OnClick({R.id.tv_tab_location, R.id.tv_tab_subway})
    void onTabClick(View v) {
        super.onFragmentClick(v);
        tabbed(v);
    }

    @OnClick({R.id.tv_subway_capital, R.id.tv_subway_busan, R.id.tv_subway_daegue, R.id.tv_subway_gj, R.id.tv_subway_dj})
    void onSubwayTabClick(View v) {
        super.onFragmentClick(v);
        subwayTabbed(v);
    }

    PCBangListenerInterface.OnDoeClickListener onDoeClickListener;
    PCBangListenerInterface.OnSiClickListener onSiClickListener;
    PCBangListenerInterface.OnLineClickListener onLineClickListener;
    PCBangListenerInterface.OnSubwayClickListener onSubwayClickListener;


    ArrayList<TextView> al_tabs;
    ArrayList<LinearLayout> al_tab_views;

    ArrayList<TextView> al_subway_categories;

    ArrayList<Doe> does;
    ArrayList<Si> sis;
    ArrayList<Line> lines;
    ArrayList<Subway> subways;

    DoeRVA doeAdapter;
    SiRVA siAdapter;
    LineRVA lineAdapter;
    SubwayRVA subwayAdapter;

    public CategoryFragment() {

    }

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        ButterKnife.bind(this, view);

        al_tabs = new ArrayList<>();
        al_tab_views = new ArrayList<>();
        al_subway_categories = new ArrayList<>();

        setTabView();
        initListener();
//        setAdapter();
        setSubwayCategory();
        setLocationView();
        setSubwayView();
        setConditionView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        does = new ArrayList<>();
        sis = new ArrayList<>();
        lines = new ArrayList<>();
        subways = new ArrayList<>();


        RealmResults<Doe> doeResults = realm.where(Doe.class).findAll();
        for (int i = 0; i < doeResults.size(); i++) {
            Doe doe = doeResults.get(i);
            if (i == 0)
                doe.setSelected(true);
            does.add(doe);
        }

        RealmResults<Si> siResults = realm.where(Si.class).equalTo("doe.name", "서울").findAll();
        for (int i = 0; i < siResults.size(); i++) {
            sis.add(siResults.get(i));
        }

        RealmResults<Region> regionResults = realm.where(Region.class).findAll();

        RealmResults<Line> lineResults = realm.where(Line.class).equalTo("region.code", "SE").findAll();
        for (int i = 0; i < lineResults.size(); i++) {
            Line line = lineResults.get(i);
            if (i == 0)
                line.setSelected(true);
            lines.add(line);
        }


        RealmResults<Subway> subwayResults = realm.where(Subway.class).equalTo("line", lineResults.get(0).getLineNum()).equalTo("region", regionResults.get(0).getCode()).findAll();
        for (int i = 0; i < subwayResults.size(); i++) {
            subways.add(subwayResults.get(i));
        }

        doeAdapter = new DoeRVA(does, onDoeClickListener);
        siAdapter = new SiRVA(sis, onSiClickListener);
        lineAdapter = new LineRVA(lines, onLineClickListener);
        subwayAdapter = new SubwayRVA(subways, onSubwayClickListener);

        setAdapter();
    }


    private void setTabView() {
        al_tabs.add(tv_tab_location);
        al_tabs.add(tv_tab_subway);

        al_tab_views.add(ll_location);
        al_tab_views.add(ll_subway);
    }

    private void initListener() {
        onDoeClickListener = new PCBangListenerInterface.OnDoeClickListener() {
            @Override
            public void onDoeClick(Doe doe) {
                RealmResults<Si> siResults = realm.where(Si.class).equalTo("doe.id", doe.getId()).findAll();
                sis.clear();
                for (int i = 0; i < siResults.size(); i++) {
                    sis.add(siResults.get(i));
                }
                siAdapter.notifyDataSetChanged();
            }
        };

        onLineClickListener = new PCBangListenerInterface.OnLineClickListener() {
            @Override
            public void onLineClick(Line line) {
                RealmResults<Subway> subwayResults = realm.where(Subway.class).equalTo("line", line.getLineNum())
                        .equalTo("region", line.getRegion().getCode()).findAll();
                subways.clear();
                for (int i = 0; i < subwayResults.size(); i++) {
                    subways.add(subwayResults.get(i));
                }
                subwayAdapter.notifyDataSetChanged();
            }
        };


        onSiClickListener = new PCBangListenerInterface.OnSiClickListener() {
            @Override
            public void onSiClick(Si si) {
                Intent intent = new Intent(getActivity(), ResultActivity.class);
                intent.putExtra("type", "si");
                intent.putExtra("id", si.getId());
                intent.putExtra("name", si.getName());
                intent.putExtra("count", si.getPcBangCount());
                startActivity(intent);
            }
        };


        onSubwayClickListener = new PCBangListenerInterface.OnSubwayClickListener() {
            @Override
            public void onSubwayClick(Subway subway) {
                Intent intent = new Intent(getActivity(), ResultActivity.class);
                intent.putExtra("type", "subway");
                intent.putExtra("id", subway.getId());
                intent.putExtra("name", subway.getName());
                intent.putExtra("count", subway.getPcBangCount());
                startActivity(intent);

            }
        };
    }

    private void setAdapter() {

        rv_doe.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_doe.setAdapter(doeAdapter);

        rv_si.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_si.setAdapter(siAdapter);

        rv_line.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_line.setAdapter(lineAdapter);

        rv_subway.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_subway.setAdapter(subwayAdapter);

    }


    private void setSubwayCategory() {
        al_subway_categories.add(tv_subway_capital);
        al_subway_categories.add(tv_subway_busan);
        al_subway_categories.add(tv_subway_daegue);
        al_subway_categories.add(tv_subway_gj);
        al_subway_categories.add(tv_subway_dj);
    }

    private void tabbed(View v) {
        for (int i = 0; i < al_tabs.size(); i++) {
            TextView textView = al_tabs.get(i);
            LinearLayout linearLayout = al_tab_views.get(i);
            if (textView.getId() == v.getId()) {
                textView.setBackgroundResource(R.drawable.border_bottom_primary);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.item_text_deep));
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                textView.setBackgroundResource(R.drawable.selector);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.item_text_basic));
                linearLayout.setVisibility(View.GONE);
            }
        }
    }

    private void subwayTabbed(View v) {
        for (int i = 0; i < al_subway_categories.size(); i++) {
            TextView textView = al_subway_categories.get(i);
            if (textView.getId() == v.getId()) {
                textView.setBackgroundResource(R.drawable.selector_grey);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.item_text_basic));
                String selectedString = textView.getText().toString();
                Region region = null;
                switch (selectedString) {
                    case "수도권":
                        region = realm.where(Region.class).equalTo("code", "SE").findFirst();
                        break;
                    case "대구":
                        region = realm.where(Region.class).equalTo("code", "DG").findFirst();
                        break;
                    case "부산":
                        region = realm.where(Region.class).equalTo("code", "BS").findFirst();
                        break;
                    case "광주":
                        region = realm.where(Region.class).equalTo("code", "GJ").findFirst();
                        break;
                    case "대전":
                        region = realm.where(Region.class).equalTo("code", "DJ").findFirst();
                        break;
                }

                RealmResults<Line> lineResults = realm.where(Line.class).equalTo("region.id", region.getId()).findAll();
                lines.clear();
                for (int j = 0; j < lineResults.size(); j++) {
                    Line line = lineResults.get(j);
                    if (j == 0)
                        line.setSelected(true);
                    lines.add(line);
                }

                RealmResults<Subway> subwayResults = realm.where(Subway.class).equalTo("line", lineResults.get(0).getLineNum())
                        .equalTo("region", region.getCode()).findAll();

                subways.clear();
                for (int j = 0; j < subwayResults.size(); j++) {
                    Subway subway = subwayResults.get(j);
                    subways.add(subway);
                }

                lineAdapter.notifyDataSetChanged();
                subwayAdapter.notifyDataSetChanged();

            } else {
                textView.setBackgroundResource(R.drawable.selector);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.item_text_deep));
            }
        }
    }

    private void setLocationView() {

    }

    private void setSubwayView() {

    }

    private void setConditionView() {

    }


    public void onLocationMenuClick() {

    }

    public void onLocationClick() {
    }

    public void onSubwayMenuClick() {

    }

    public void onSubwayClick() {

    }


}

package com.mobipos.app.Admin.DashboardFragments;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mobipos.app.Admin.Adapters.CustomAdapter;
import com.mobipos.app.Admin.RowItem;
import com.mobipos.app.R;

import java.util.ArrayList;
import java.util.List;


public class NotificationsFragment  extends Fragment {
    String[] member_names;
    TypedArray profile_pics;
    String[] statues;
    String[] contactType;

    List<RowItem> rowItems;
    ListView mylistview;

    public static NotificationsFragment newInstance(){
        NotificationsFragment fragment= new NotificationsFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_notifications_fragment, container, false);


    }
    public void onViewCreated (View view,Bundle savedInstanceState){
        rowItems = new ArrayList<RowItem>();

        member_names = getResources().getStringArray(R.array.names_array);

        profile_pics = getResources().obtainTypedArray(R.array.pics_array);

        statues = getResources().getStringArray(R.array.sta_array);

        contactType = getResources().getStringArray(R.array.contact_array);




        for (int i = 0; i < member_names.length; i++) {
            RowItem item = new RowItem(member_names[i],
                    profile_pics.getResourceId(i, -1), statues[i],
                    contactType[i]);
            rowItems.add(item);
        }

        mylistview = view.findViewById(R.id.list);
        CustomAdapter adapter = new CustomAdapter(getContext(), rowItems);
        mylistview.setAdapter(adapter);
        profile_pics.recycle();
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String member_name = rowItems.get(i).getMember_name();
                Toast.makeText(getActivity(), "" + member_name,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
}
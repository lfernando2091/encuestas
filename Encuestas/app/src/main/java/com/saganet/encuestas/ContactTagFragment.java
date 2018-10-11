package com.saganet.encuestas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactTagFragment extends DialogFragment {

    public static final int REQUEST_INPUT_TAG = 7;
    private Button mButtonCancel;
    private Button mButtonOk;
    private View linearLayout;
    private RadioButton[] rbtn;
    private RadioGroup rgp;
    private String mResultDataRbtn="";
    public static String mResultDataRbtn_TRUE="";
    public static final String RESULT_OK_TAG="OK_TAG";
    public static final String RESULT_CANCELED_TAG="CANCELED_TAG";
    public static final String RESULT_TYPE="ListResult";
    private LinearLayout.LayoutParams llp;
    private ArrayList<String> spinnerArray;
    private ArrayList<String> spinnerIdArray;
    DataSesion mTagDbHelper;
    ProgressDialog pd = null;

    public ContactTagFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }
    public static ContactTagFragment newInstance(String title) {
        ContactTagFragment frag = new ContactTagFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_tag, container);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        linearLayout = (View) view.findViewById(R.id.tag_content_t);
        mButtonCancel = (Button) view.findViewById(R.id.b_cancel_t);
        mTagDbHelper= new DataSesion(getActivity());
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFinishAsk(false);
            }
        });
        mButtonOk = (Button) view.findViewById(R.id.b_ok_t);
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mResultDataRbtn.isEmpty()){
                    DataUpload.USER_ACCOUNT_ENC_ESTADO_ID=mResultDataRbtn;
                    mResultDataRbtn_TRUE=mResultDataRbtn;
                    mResultDataRbtn="";
                    showFinishAsk(true);
                } else {
                    mResultDataRbtn="";
                        Toast.makeText(getActivity(), "Ningun elemento seleccionado", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        new GetAnswerTask().execute();
    }
    private void showFinishAsk(boolean q){
        //getActivity().setResult(q? Activity.RESULT_OK: Activity.RESULT_CANCELED);
        Intent intent = new Intent();
        intent.putExtra(RESULT_TYPE, q? RESULT_OK_TAG: RESULT_CANCELED_TAG);
        getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_INPUT_TAG, intent);
        getDialog().dismiss();
    }

    private class GetAnswerTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setTitle("Cargando...");
            pd.setMessage("Espere por favor.");
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected Cursor doInBackground(Void... params) {
            return mTagDbHelper.getEstadoEncuesta();
        }
        @Override
        protected void onPostExecute(Cursor cursor){
            if (pd != null) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                pd = null;
            }
            if(cursor!= null && cursor.moveToLast()){
                spinnerArray = new ArrayList<String>();
                spinnerIdArray= new ArrayList<String>();
                if (cursor.moveToFirst()) {
                    do {
                        // Adding contact to list
                        spinnerArray.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ENCUESTA_ESTADO)));
                        spinnerIdArray.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_PKEY)));
                    } while (cursor.moveToNext());
                }
                        //CONSTRIUR UNA LISTA SI LOS DATOS SON MENORES A 6
                        bluiltList(cursor.getCount());
            }else {
            }
        }
    }


    private void bluiltList(int n){
        llp = new LinearLayout.LayoutParams(CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT, CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(5, 50, 0, 0);
        //Toast.makeText(getActivity(),String.valueOf(n), Toast.LENGTH_SHORT).show();
        rgp= new RadioGroup(getActivity());
        rbtn= new RadioButton[n];
        for(int i=0; i<n;i++){
            rbtn[i]  = new RadioButton(getActivity());
            rbtn[i].setText(spinnerArray.get(i));
            rbtn[i].setTextSize(20);
            rbtn[i].setId(i);
            rbtn[i].setLayoutParams(llp);
            rbtn[i].setTag(spinnerIdArray.get(i));
            rgp.addView(rbtn[i]);
        }
        //set listener to radio button group
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mResultDataRbtn= rbtn[checkedId].getTag().toString();
            }
        });
        ((LinearLayout) linearLayout).addView(rgp);
    }
}

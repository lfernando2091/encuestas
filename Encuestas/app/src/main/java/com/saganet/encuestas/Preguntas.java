package com.saganet.encuestas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //Preguntas.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Preguntas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Preguntas extends Fragment {
    private static final String ARG_ENCUESTA_ID="encueestaid";
    //public static final int REQUEST_SAVED_ENCUESTA = 5;
    private String mAccountID;
    private CollapsingToolbarLayout mCollapseingView;
    private Spinner spinner;
    private FloatingActionButton fab;
    private ImageView mAvatar;
    private View linearLayout;
    private TextView valueTV;
    private EditText editText;
    private ArrayList<String> spinnerArray;
    private ArrayList<String> spinnerIdArray;
    private ArrayList<String> spinnerAccionTipoArray;
    private String currentAccionTipo;
    private ArrayList<String> spinnerAccionValorArray;
    private String currentAccionValor;
    private ArrayList<String> spinnerIdEstEncArray;
    private String currentIdEstEnc;
    private ArrayList<String> spinnerResultArray;
    private LinearLayout.LayoutParams llp;
    private CheckBox[] chk;
    private RadioButton[] rbtn;
    private RadioGroup rgp;
    private Button mSelectFech;
    private static TextView mFecha;
    public static int mDay=0;
    public static int mMes=0;
    public static int mYear=0;
    private int MAX_CCHECK_BOX=0;
    private String mResultDataRbtn="";
    private boolean mWaitRasultToNext=false, isSaltarAPregunta=false, isTerminar=false;
    private boolean mBuildenComboMin=false, mFinishResult=false;
    private int n,m,LAST_USER_CUESTIONARIO_COUNT;
    ProgressDialog pd = null;
    private int currentItem = 0;
    private Cursor cur;
    private String RespuestaCargar="";

    DataSesion mAccountDbHelper;
    public Preguntas() {
        // Required empty public constructor
    }

    public static Preguntas newInstance(String accountid) {
        Preguntas fragment = new Preguntas();
        Bundle args = new Bundle();
        args.putString(ARG_ENCUESTA_ID, accountid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAccountID = getArguments().getString(ARG_ENCUESTA_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_preguntas, container, false);
        mAvatar= (ImageView) getActivity().findViewById(R.id.iv_avatar);
        mCollapseingView=(CollapsingToolbarLayout)  getActivity().findViewById(R.id.toolbar_layout);
        mAccountDbHelper= new DataSesion(getActivity());
        linearLayout= (View)  v.findViewById(R.id.sec_preguntas);
        mCollapseingView.setTitle(DataUpload.USER_NAME);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.btnext);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ShowCurrentVideo();
                if(isSaltarAPregunta){
                    getNextAskOnlyIfJump();
                } else if(isTerminar){
                    getNextAskFinish();
                } else {
                    getNextAsk();
                }
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //.setAction("Action", null).show();
            }
        });
        String avatarUri = "redback.jpg";
        Glide
                .with(this)
                .load(Uri.parse("file:///android_asset/" + avatarUri))
                .error(R.drawable.ic_account_circle)
                .centerCrop()
                .into(mAvatar);
        loadAsk();

        return v;
    }

    private void ShowCurrentVideo(){
        Intent intent = new Intent(getActivity(), VideoBase.class);
        startActivity(intent);
    }

    private void loadAsk(){new GetAskTask().execute();}

    private class GetAskTask extends AsyncTask<Void, Void, Cursor>{
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
            //Log.v("id :", mAccountID);
            return mAccountDbHelper.getPreguntaId(DataUpload.USER_ACCOUNT_ENC_ID);
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
                spinnerResultArray= new ArrayList<String>();
                String s= cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_TIPO_PREGUNTA));
                DataUpload.TypeAsk= DataUpload.tipoPregunta.valueOf(s);
                        DataUpload.USER_CUESTIONARIO_COUNT=0;
                        spinnerResultArray.add(DataUpload.USER_CUESTIONARIO_COUNT,"null");
                        ((LinearLayout) linearLayout).removeAllViews();
                        llp = new LinearLayout.LayoutParams(CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT, CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT);
                        llp.setMargins(5, 50, 0, 0); // llp.setMargins(left, top, right, bottom);
                        //Data.p= Data.db.getPreguntas(countAsk);
                        valueTV = new TextView(getActivity());
                        valueTV.setText(DataUpload.pregList.get(DataUpload.USER_CUESTIONARIO_COUNT));
                        //valueTV.setId(0);
                        valueTV.setLayoutParams(llp);
                        valueTV.setTextSize(24);
                        ((LinearLayout) linearLayout).addView(valueTV);
                setOptionsResultAsk(DataUpload.TypeAsk);
            }else {
                showLoadError();
            }
            //cursor.close();
        }
    }
    private class GetNextAskTask extends AsyncTask<Void, Void, Cursor>{
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
            return mAccountDbHelper.getPreguntaIdIn(DataUpload.ptypeList.get(DataUpload.USER_CUESTIONARIO_COUNT));
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
                ((LinearLayout) linearLayout).removeAllViews();
                String s= cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_TIPO_PREGUNTA));
                DataUpload.TypeAsk= DataUpload.tipoPregunta.valueOf(s);
                llp = new LinearLayout.LayoutParams(CollapsingToolbarLayout.LayoutParams.MATCH_PARENT, CollapsingToolbarLayout.LayoutParams.MATCH_PARENT);
                llp.setMargins(5, 50, 5, 0); // llp.setMargins(left, top, right, bottom);
                //Data.p= Data.db.getPreguntas(countAsk);
                valueTV = new TextView(getActivity());
                valueTV.setText(DataUpload.pregList.get(DataUpload.USER_CUESTIONARIO_COUNT));
                valueTV.setId(Integer.parseInt(DataUpload.pIDList.get(DataUpload.USER_CUESTIONARIO_COUNT)));
                valueTV.setLayoutParams(llp);
                valueTV.setTextSize(24);
                ((LinearLayout) linearLayout).addView(valueTV);
                setOptionsResultAsk(DataUpload.TypeAsk);
                //showAsk(new tPreguntas(cursor));
            }else {
                showLoadError();
            }
            //cursor.close();
        }
    }
    private void setOptionsResultAsk(DataUpload.tipoPregunta t){
        switch (t){
            case CerradaMultiple:
               DataUpload.isMultiple=true;
                new GetAnswerTask().execute();
                break;
            case CerradaSimple:
                DataUpload.isMultiple=false;
                new GetAnswerTask().execute();
                break;
            case CerradaFecha:
                mSelectFech= new Button(getActivity());
                mFecha= new TextView(getActivity());
                mFecha.setId(0);
                mFecha.setTextSize(24);
                mFecha.setLayoutParams(llp);
                mSelectFech.setText("Ingresar fecha");
                mSelectFech.setTextSize(24);
                mSelectFech.setId(1+10);
                mSelectFech.setLayoutParams(llp);
                mSelectFech.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DataUpload.UPDATE_PREGUNTAS_COMPONENT=true;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        DialogFragment newFragment = new TimeSet();
                        newFragment.show(ft, "datePicker");
                    }
                });
                final Calendar c= Calendar.getInstance();
                mYear= c.get(Calendar.YEAR);
                mMes= c.get(Calendar.MONTH);
                mDay= c.get(Calendar.DAY_OF_MONTH);
                if(DataUpload.IS_REDITABLE_ENCUESTA_ID) {
                    getStringValueAnswer();
                    mFecha.setText(RespuestaCargar);
                }else{
                    updateDisplay();
                }
                ((LinearLayout) linearLayout).addView(mFecha);
                ((LinearLayout) linearLayout).addView(mSelectFech);
                //new GetAskTask().execute();
                break;
            case AbiertaMultipleLinea:
                editText = new EditText(getActivity());
                editText.setHint("Respuesta larga");
                editText.setId(0);
                editText.setText("");
                editText.setLayoutParams(llp);
                editText.setTextSize(24);
                editText.setSingleLine(false);
                editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                if(DataUpload.IS_REDITABLE_ENCUESTA_ID){
                    getStringValueAnswer();
                    editText.setText(RespuestaCargar.equals("-")?"":RespuestaCargar);
                }
                ((LinearLayout) linearLayout).addView(editText);
                break;
            case AbiertaSimpleLinea:
                editText = new EditText(getActivity());
                editText.setHint("Respuesta corta");
                editText.setId(0);
                editText.setText("");
                editText.setSingleLine(true);
                editText.setLayoutParams(llp);
                editText.setTextSize(24);
                if(DataUpload.IS_REDITABLE_ENCUESTA_ID){
                    getStringValueAnswer();
                    editText.setText(RespuestaCargar.equals("-")?"":RespuestaCargar);
                }
                ((LinearLayout) linearLayout).addView(editText);
                break;
        }
    }
    // updates the date in the TextView
    public static void updateDisplay() {
        int n= mMes+1;
        String sn=String.valueOf(n);
        if(n<10){
            sn="";
            sn= "0"+ String.valueOf(n);
        }
        int k= mDay;
        String kn=String.valueOf(k);
        if(k<10){
            kn="";
            kn= "0"+ String.valueOf(k);
        }
        mFecha.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(kn).append("/")
                        .append(sn).append("/")
                        .append(mYear).append(" "));
    }

    private void getStringValueAnswer(){
        cur=mAccountDbHelper.getRespuestasDone();
        cur.moveToFirst();
        RespuestaCargar=cur.getString(cur.getColumnIndex("p_"+String.valueOf(DataUpload.USER_CUESTIONARIO_COUNT+1)));
        Log.v("-----Respuesta:",RespuestaCargar);
    }

    private class GetAnswerTask extends AsyncTask<Void, Void, Cursor>{
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
            if(DataUpload.IS_REDITABLE_ENCUESTA_ID){
                //Cargar los datos por defecto
                getStringValueAnswer();
            }
            return mAccountDbHelper.getRespuestasPId(DataUpload.pIDList.get(DataUpload.USER_CUESTIONARIO_COUNT));
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
                spinnerAccionTipoArray= new ArrayList<String>();
                spinnerAccionValorArray= new ArrayList<String>();
                spinnerIdEstEncArray= new ArrayList<String>();
                if (cursor.moveToFirst()) {
                    //Agregar una opcion más para el combobox valor 0
                    if(!(cursor.getCount()<=DataUpload.ANSWERS_MINIMAL_TO_BLUILT)){
                        if(!DataUpload.isMultiple){
                            spinnerArray.add(0,"Selecciona respuesta");
                            spinnerIdArray.add(0,"0");
                            spinnerAccionTipoArray.add(0,"Siguiente");
                            spinnerAccionValorArray.add(0,"0");
                            spinnerIdEstEncArray.add(0,"0");
                        }
                    }
                    do {
                        // Adding contact to list
                        spinnerArray.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_RESPUESTA)));
                        spinnerIdArray.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_PKEY)));
                        spinnerAccionTipoArray.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ACCION_TIPO)));
                        spinnerAccionValorArray.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ACCION_VALOR)));
                        spinnerIdEstEncArray.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ID_ENCUESTA_ESTADO)));
                    } while (cursor.moveToNext());
                }
                if(cursor.getCount()<=DataUpload.ANSWERS_MINIMAL_TO_BLUILT){
                    if(DataUpload.isMultiple){
                        mBuildenComboMin=false;
                        builtCheckbox(cursor.getCount());
                    } else {
                        mBuildenComboMin=false;
                        //CONSTRIUR UNA LISTA SI LOS DATOS SON MENORES A 6
                        bluiltList(cursor.getCount());
                    }
                } else{
                    if(DataUpload.isMultiple){
                        mBuildenComboMin=false;
                        builtCheckbox(cursor.getCount());
                    } else {
                        mBuildenComboMin=true;
                        //CONSTRIUR UN COMBOBOX
                        bluitComboBox();
                    }
                }
            }else {
                showLoadError();
            }
            //cursor.close();
        }
    }

    private void builtCheckbox(int n){
        chk= new CheckBox[n];
        MAX_CCHECK_BOX=n;
        for(int i=0; i<n;i++){
            chk[i]  = new CheckBox(getActivity());
            chk[i].setText(spinnerArray.get(i));
            chk[i].setLayoutParams(llp);
            chk[i].setTextColor(Color.BLACK);
            chk[i].setTextSize(30);
            chk[i].setId(i);
            chk[i].setTag(spinnerIdArray.get(i));
            if(DataUpload.IS_REDITABLE_ENCUESTA_ID){
                setCheckLoad(chk[i]);
            }
            ((LinearLayout) linearLayout).addView(chk[i]);
        }
    }
    private String getSelectedCheckbox(){
        String n="";
        for(int i=0; i<MAX_CCHECK_BOX; i++){
            if(chk[i].isChecked()){
                if(n.toString().equals("")){
                    n= String.valueOf(chk[i].getTag().toString());
                }else {
                    n= n+ "|" +String.valueOf(chk[i].getTag().toString());
                }
            }
        }
        return n;
    }

    private void setCheckLoad(CheckBox c){
        Log.v("Respuesta",RespuestaCargar);
        Log.v("Tiene comas",String.valueOf(RespuestaCargar.contains("|")));
        if(RespuestaCargar.contains("|")){
            Log.v("Tiene comas", "");
            ArrayList<String> items = new  ArrayList<String>(Arrays.asList(RespuestaCargar.split("\\|")));
            for(String s: items){
                Log.v("Get last answer: ",s);
                if(c.getTag().equals(s)){
                    c.setChecked(true);
                }
            }
        }
        else {
            Log.v("No tiene comas", "");
            if(c.getTag().equals(RespuestaCargar)){
                c.setChecked(true);
            }
        }
    }

    private void bluitComboBox(){
        spinner = new Spinner(getActivity());
        //valueTV.setId(0);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setLayoutParams(llp);
        //spinner.setBackground(Color.parseColor("#ff2828"));
        spinner.setAdapter(spinnerArrayAdapter);
        if(DataUpload.IS_REDITABLE_ENCUESTA_ID){
            int res=0;
            for(int i=0;i<spinnerIdArray.size();i++){
                if(spinnerIdArray.get(i).equals(RespuestaCargar)){
                    res=i;
                }
            }
            spinner.setSelection(res);
        }else {
            spinner.setSelection(0);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(currentItem == position){
                    return; //do nothing
                }
                else
                {
                    Log.v("SELECTED: ",String.valueOf(position));
                    currentAccionTipo=  spinnerAccionTipoArray.get(position);
                    DataUpload.tipoAccion= DataUpload.eTypeAction.valueOf(currentAccionTipo);
                    switch (DataUpload.tipoAccion){
                        case Siguiente:
                            //n=DataUpload.USER_CUESTIONARIO_COUNT;
                            //m=DataUpload.pIDList.size();
                            isSaltarAPregunta=false;
                            isTerminar=false;
                            break;
                        case SaltarAPregunta:
                            isSaltarAPregunta=true;
                            isTerminar=false;
                            currentAccionValor=  spinnerAccionValorArray.get(position);
                            break;
                        case Terminar:
                            isSaltarAPregunta=false;
                            isTerminar=true;
                            currentIdEstEnc= spinnerIdEstEncArray.get(position);
                            DataUpload.USER_ACCOUNT_ENC_ESTADO_ID=currentIdEstEnc;
                            break;
                    }
                    //write your code here
                }
                currentItem = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ((LinearLayout) linearLayout).addView(spinner);
    }
    private void bluiltList(int n){
        //Toast.makeText(getActivity(),String.valueOf(n), Toast.LENGTH_SHORT).show();
        rgp= new RadioGroup(getActivity());
        rbtn= new RadioButton[n];
        //set listener to radio button group
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //int checkedRadioButtonId = rgp.getCheckedRadioButtonId();
                //RadioButton radioBtn = findViewById_(checkedId);
                //Toast.makeText(getActivity(),String.valueOf(checkedId), Toast.LENGTH_SHORT).show();
                mResultDataRbtn= rbtn[checkedId].getTag().toString();
                currentAccionTipo=  spinnerAccionTipoArray.get(checkedId);
                DataUpload.tipoAccion= DataUpload.eTypeAction.valueOf(currentAccionTipo);
                switch (DataUpload.tipoAccion){
                    case Siguiente:
                        //n=DataUpload.USER_CUESTIONARIO_COUNT;
                        //m=DataUpload.pIDList.size();
                        isSaltarAPregunta=false;
                        isTerminar=false;
                        break;
                    case SaltarAPregunta:
                        isSaltarAPregunta=true;
                        isTerminar=false;
                        currentAccionValor=  spinnerAccionValorArray.get(checkedId);
                        break;
                    case Terminar:
                        isSaltarAPregunta=false;
                        isTerminar=true;
                        currentIdEstEnc= spinnerIdEstEncArray.get(checkedId);
                        DataUpload.USER_ACCOUNT_ENC_ESTADO_ID=currentIdEstEnc;
                        Log.v("Valor ","selected "+DataUpload.USER_ACCOUNT_ENC_ESTADO_ID);
                        break;
                }
                //mResultDataRbtn="2";
                //Toast.makeText(getActivity(),String.valueOf(checkedId), Toast.LENGTH_SHORT).show();
            }
        });
        for(int i=0; i<n;i++){
            rbtn[i]  = new RadioButton(getActivity());
            rbtn[i].setText(spinnerArray.get(i));
            rbtn[i].setTextSize(30);
            rbtn[i].setId(i);
            rbtn[i].setLayoutParams(llp);

            //Cambiamo la entrada de ID por TEXT plano
            //rbtn[i].setTag(spinnerIdArray.get(i));
            rbtn[i].setTag(spinnerArray.get(i));

            //RespuestaCargar
            if(rbtn[i].getTag().equals(RespuestaCargar)){
                rbtn[i].setChecked(true);
            }
            rgp.addView(rbtn[i]);
        }
        ((LinearLayout) linearLayout).addView(rgp);
    }
    private void FillEmpty(int from, int to){
        //spinnerResultArray.add(from+1,mResultDataRbtn);
        for (int i=from+1;i<=to;i++){
            spinnerResultArray.add(i,"-");
            Log.v("Valor","AUTORELLENADO DE DATOS");
            Log.v("Valor",String.valueOf(spinnerResultArray.size()));
        }
    }
    private void showFinishAsk(boolean q){
        if(!q){
            showError();
        }
        getActivity().setResult(q?Activity.RESULT_OK: Activity.RESULT_CANCELED);
        getActivity().finish();
    }
    private void showError(){
        Toast.makeText(getActivity(),"Error al finalizar cuestionario",Toast.LENGTH_SHORT).show();
    }
    private void showLoadError(){
        mCollapseingView.setTitle(DataUpload.USER_NAME);
        Toast.makeText(getActivity(),"Error al cargar cuestionario",Toast.LENGTH_SHORT).show();
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem M){
        switch (M.getItemId()){
            case R.id.action_nxt:
                if(isSaltarAPregunta){
                    getNextAskOnlyIfJump();
                }else if(isTerminar){
                    getNextAskFinish();
                }else {
                    getNextAsk();
                }
                break;
        }
        return  super.onOptionsItemSelected(M);
    }

    private void getNextAsk(){
        Log.v("Valor","continuar siguiente");
        SeenResultDataType(false);
        n=DataUpload.USER_CUESTIONARIO_COUNT;
        m=DataUpload.pIDList.size();
        if((n<m) && mWaitRasultToNext){
            //Log.v("Siguiente valor",String.valueOf(DataUpload.USER_CUESTIONARIO_COUNT++));
            DataUpload.USER_CUESTIONARIO_COUNT+=1;
            Log.v("Valor pIDList",String.valueOf(DataUpload.pIDList.size()));
            Log.v("Valor COUNT",String.valueOf(DataUpload.USER_CUESTIONARIO_COUNT));
            SeenResultDataType(true);
            mWaitRasultToNext=false;
            if(n<m-1){
                isTerminar=false;
                isSaltarAPregunta=false;
                new GetNextAskTask().execute();
            }
        }if(mFinishResult){
            //WHEN RESULT ALL THE ASK TO DO, WHY I DO? //IMPLEMENTS ALL WHEN FINISH THE TEST
            DataUpload.NUMBER_ANSWER_TO_SAVE=spinnerResultArray.size();
            DataUpload.USER_ACCOUNT_ENC_ESTADO_ID=
                    DataUpload.USER_ACCOUNT_ENC_ESTADO_ID==null?
                            "8":
                            DataUpload.USER_ACCOUNT_ENC_ESTADO_ID;
            DataUpload.VALUE_FINALIZED_TIME=(DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date()).toString());
            Log.v("Valor pregutas array ",String.valueOf(spinnerResultArray.size()));
            Log.v("Valor pregutas int",String.valueOf(DataUpload.USER_CUESTIONARIO_COUNT));
            tRespuestasEncuesta tre= new tRespuestasEncuesta(
                    Integer.parseInt(DataUpload.USER_ID),
                    DataUpload.USER_ACCOUNT_ID,
                    Integer.parseInt(DataUpload.USER_ACCOUNT_ENC_ID),
                    Integer.parseInt(DataUpload.USER_ACCOUNT_ENC_ESTADO_ID),
                    DataUpload.VALUE_INITIALIZED_TIME,
                    DataUpload.VALUE_FINALIZED_TIME,
                    DataUpload.VALUE_LATITUDE_POSITION,
                    DataUpload.VALUE_LONGITUDE_POSITION,
                    "0","0",
                    spinnerResultArray);
            mFinishResult=false;
            Log.v("USER ID: ",DataUpload.USER_ID);
            Log.v("CONTACTO ID: ",DataUpload.USER_ACCOUNT_ID);
            //Log.v("ENCUESTA ID: ",DataUpload.USER_ACCOUNT_ENC_ID);
            new SaveTaskDataEncuesta().execute(tre);
        }
    }
    private void getNextAskOnlyIfJump(){
        Log.v("Valor","Salto ha pregunta");
        SeenResultDataType(false);
        n=DataUpload.USER_CUESTIONARIO_COUNT;
        m=DataUpload.pIDList.size();
        if((n<m) && mWaitRasultToNext){
            DataUpload.USER_CUESTIONARIO_COUNT+=1;
            SeenResultDataType(true);
            LAST_USER_CUESTIONARIO_COUNT=DataUpload.USER_CUESTIONARIO_COUNT;
            Log.v("Valor salto desde",String.valueOf(LAST_USER_CUESTIONARIO_COUNT));
            String getActualIDPosition= DataUpload.pIDList.get(LAST_USER_CUESTIONARIO_COUNT+1);
            Log.v("Valor altual pos",String.valueOf(getActualIDPosition));
            DataUpload.USER_CUESTIONARIO_COUNT=Integer.parseInt(currentAccionValor);
            Log.v("Valor action pos",String.valueOf(DataUpload.USER_CUESTIONARIO_COUNT));
            int dif=DataUpload.USER_CUESTIONARIO_COUNT-Integer.parseInt(getActualIDPosition);
            Log.v("Valor dif pos",String.valueOf(dif));
            dif=dif+LAST_USER_CUESTIONARIO_COUNT;
            DataUpload.USER_CUESTIONARIO_COUNT=dif+1;
            Log.v("Valor here pos",String.valueOf(LAST_USER_CUESTIONARIO_COUNT));
            Log.v("Valor next pos",String.valueOf(DataUpload.USER_CUESTIONARIO_COUNT));
            FillEmpty(LAST_USER_CUESTIONARIO_COUNT, DataUpload.USER_CUESTIONARIO_COUNT);
            mWaitRasultToNext=false;
            if(n<m-1){
                isTerminar=false;
                isSaltarAPregunta=false;
                new GetNextAskTask().execute();
            }
        }
    }
    private void getNextAskFinish(){
        Log.v("Valor","terminar encuesta");
        SeenResultDataType(false);
        n=DataUpload.USER_CUESTIONARIO_COUNT;
        m=DataUpload.pIDList.size();
        if((n<m) && mWaitRasultToNext){
            DataUpload.USER_CUESTIONARIO_COUNT+=1;
            SeenResultDataType(true);
            FillEmpty(DataUpload.USER_CUESTIONARIO_COUNT, m);
            mWaitRasultToNext=false;
            mFinishResult=true;
            isTerminar=false;
            isSaltarAPregunta=false;
        }if(mFinishResult){
            //WHEN RESULT ALL THE ASK TO DO, WHY I DO? //IMPLEMENTS ALL WHEN FINISH THE TEST
            DataUpload.NUMBER_ANSWER_TO_SAVE=spinnerResultArray.size();
            Log.v("Columns 1: ",String.valueOf(spinnerResultArray.size()));
            DataUpload.USER_ACCOUNT_ENC_ESTADO_ID=
                    DataUpload.USER_ACCOUNT_ENC_ESTADO_ID==null?
                            "8":
                            DataUpload.USER_ACCOUNT_ENC_ESTADO_ID;
            DataUpload.VALUE_FINALIZED_TIME=(DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date()).toString());
            Log.v("Numero pregutas array ",String.valueOf(spinnerResultArray.size()));
            Log.v("Numero pregutas int",String.valueOf(DataUpload.USER_CUESTIONARIO_COUNT));
            //DataUpload.USER_CUESTIONARIO_COUNT
            tRespuestasEncuesta tre= new tRespuestasEncuesta(Integer.parseInt(DataUpload.USER_ID),
                    DataUpload.USER_ACCOUNT_ID,
                    Integer.parseInt(DataUpload.USER_ACCOUNT_ENC_ID),
                    Integer.parseInt(DataUpload.USER_ACCOUNT_ENC_ESTADO_ID),
                    DataUpload.VALUE_INITIALIZED_TIME,
                    DataUpload.VALUE_FINALIZED_TIME,
                    DataUpload.VALUE_LATITUDE_POSITION,
                    DataUpload.VALUE_LONGITUDE_POSITION,
                    "0","0",
                    spinnerResultArray);
            Log.v("Columns 2: ",String.valueOf(spinnerResultArray.size()));
            mFinishResult=false;
            Log.v("USER ID: ",DataUpload.USER_ID);
            Log.v("CONTACTO ID: ",DataUpload.USER_ACCOUNT_ID);
            //Log.v("ENCUESTA ID: ",DataUpload.USER_ACCOUNT_ENC_ID);
            new SaveTaskDataEncuesta().execute(tre);
        }
    }
    public void getForceFinish(){
        Log.v("Valor","terminar encuesta truncamiento de estado");
        //--SeenResultDataType(false);
        Boolean action=false;
        try {
            if(!spinnerResultArray.get(1).toString().equals("")) {
                DataUpload.CAN_RETURN_EMPTY_ENCUESTA=false;
                action=true;
            }
            else {
                DataUpload.CAN_RETURN_EMPTY_ENCUESTA=true;
                action=false;
            }
        }catch (Exception e){
            DataUpload.CAN_RETURN_EMPTY_ENCUESTA=true;
            action=false;
        }
        if (action) {
            n=DataUpload.USER_CUESTIONARIO_COUNT;
            m=DataUpload.pIDList.size();
            mWaitRasultToNext=true;
            if((n<m) && mWaitRasultToNext){
                //-----DataUpload.USER_CUESTIONARIO_COUNT+=1;
                //-----SeenResultDataType(true);
                FillEmpty(DataUpload.USER_CUESTIONARIO_COUNT, m);
                mWaitRasultToNext=false;
                mFinishResult=true;
                isTerminar=false;
                isSaltarAPregunta=false;
            }
        }
        if(mFinishResult){
            //WHEN RESULT ALL THE ASK TO DO, WHY I DO? //IMPLEMENTS ALL WHEN FINISH THE TEST
            DataUpload.NUMBER_ANSWER_TO_SAVE=spinnerResultArray.size();
            Log.v("Columns 1: ",String.valueOf(spinnerResultArray.size()));
            DataUpload.USER_ACCOUNT_ENC_ESTADO_ID= "2";
            DataUpload.VALUE_FINALIZED_TIME=(DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date()).toString());
            Log.v("Numero pregutas array ",String.valueOf(spinnerResultArray.size()));
            Log.v("Numero pregutas int",String.valueOf(DataUpload.USER_CUESTIONARIO_COUNT));
            //DataUpload.USER_CUESTIONARIO_COUNT
            tRespuestasEncuesta tre= new tRespuestasEncuesta(Integer.parseInt(DataUpload.USER_ID),
                    DataUpload.USER_ACCOUNT_ID,
                    Integer.parseInt(DataUpload.USER_ACCOUNT_ENC_ID),
                    Integer.parseInt(DataUpload.USER_ACCOUNT_ENC_ESTADO_ID),
                    DataUpload.VALUE_INITIALIZED_TIME,
                    DataUpload.VALUE_FINALIZED_TIME,
                    DataUpload.VALUE_LATITUDE_POSITION,
                    DataUpload.VALUE_LONGITUDE_POSITION,
                    "0","0",
                    spinnerResultArray);
            Log.v("Columns 2: ",String.valueOf(spinnerResultArray.size()));
            mFinishResult=false;
            Log.v("USER ID: ",DataUpload.USER_ID);
            Log.v("CONTACTO ID: ",DataUpload.USER_ACCOUNT_ID);
            //Log.v("ENCUESTA ID: ",DataUpload.USER_ACCOUNT_ENC_ID);
            new SaveTaskDataEncuesta().execute(tre);
        }
    }

    private class SaveTaskDataEncuesta extends AsyncTask<tRespuestasEncuesta, Void, Boolean> {
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
        protected Boolean doInBackground(tRespuestasEncuesta... trespuestasencuest) {
            //if(mAccountId!=null){
            //} else {
            if(DataUpload.IS_REDITABLE_ENCUESTA_ID){
                return mAccountDbHelper.UpdateEncuestaResult(trespuestasencuest[0])>0;
            } else {
                DataUpload.IS_REDITABLE_ENCUESTA_ID=false;
                return mAccountDbHelper.InsertEncuestaResult(trespuestasencuest[0])>0;
            }
            //}
        }
        @Override
        protected void onPostExecute(Boolean res) {
            if (pd != null) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                pd = null;
            }
            DataUpload.IS_REDITABLE_ENCUESTA_ID=false;showFinishAsk(res);
        }
    }

    private void SeenResultDataType(boolean b){
        String s = "";
        switch (DataUpload.TypeAsk){
            case CerradaFecha:
                s= mFecha.getText().toString();
                break;
            case CerradaMultiple:
                s= getSelectedCheckbox();
                break;
            case CerradaSimple:
                if(mBuildenComboMin){

                    //Cambiar capturado del ID por texto PLANO
                    s= spinnerIdArray.get(spinner.getSelectedItemPosition());
                    if(s.equals("0")){
                        s="";
                    }
                }else {
                        s=mResultDataRbtn;//mResultDataRbtn=mResultDataRbtn.toString().equals("")?mResultDataRbtn:"";
                }
                break;
            case AbiertaMultipleLinea:
                s=editText.getText().toString();
                break;
            case AbiertaSimpleLinea:
                s=editText.getText().toString();
                break;
        }
        //Valida contenido distinto que nada
        //MODIFICADO EL 31 POR isEmpty ----toString().equals("")
        if(s.isEmpty()){
            if(!b){
                //Mensaje para cuando los campos no estan contestados
                //Toast.makeText(getActivity(),"Pregunta no contestada",Toast.LENGTH_SHORT).show();
                ShowAlertDialog("La pregunta requiere ser contestada.");
                if(n==m-1){
                    mFinishResult=false;
                }
                mWaitRasultToNext=false;
                Log.v("Datos vacios", "empty");
            }
            //MODIFICADO EL 31 POR isEmpty ----toString().equals("")
        }else if(!s.isEmpty()){
            //Verificar si el dato se va aguardar o solo evaluar
            if(b){
                spinnerResultArray.add(DataUpload.USER_CUESTIONARIO_COUNT,s.toString());
                mResultDataRbtn="";
                if(n==m-1){
                    mFinishResult=true;
                }
                Log.v("Datos agregados", s.toString());
            }else {
                mWaitRasultToNext=true;
                Log.v("Datos por agregar", "comenzando....");
            }
        }
    }

    private  void ShowAlertDialog(String s){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setTitle("Aviso");
        builder1.setMessage(s);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}

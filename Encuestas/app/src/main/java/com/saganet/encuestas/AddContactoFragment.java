package com.saganet.encuestas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.design.widget.TextInputEditText;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //AddContactoFragment.//OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddContactoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddContactoFragment extends Fragment{

    //private OnFragmentInteractionListener mListener;
    DataSesion dbsEncuesta;
    private static final String ARG_CONTACT_ID = "arg_contact_id";
    private String mAccountId;
    ArrayList<String> spinnerArray;
    ArrayList<String> spinnerEstados;
    private FloatingActionButton mSaveButton;
    private TextView mAcctEncuesta;
    private TextInputEditText mNombre;
    private TextInputEditText mPaterno;
    private TextInputEditText mMaterno;
    private TextInputEditText mCalle;
    private TextInputEditText mNColinia;
    private TextInputEditText mNumExt;
    private TextInputEditText mNumInt;
    private TextInputEditText mNumCodPostal;
    private Spinner mEntidad;
    private TextInputEditText mMunicipio;
    private static TextView mFecNac;
    private static String mFecNacText;
    private static Button mSelectFech;
    private Spinner mGenero;
    public static int mDay=0;
    public static int mMes=0;
    public static int mYear=0;
    ProgressDialog pd = null;

    public AddContactoFragment() {
        // Required empty public constructor
    }
    public static AddContactoFragment newInstance(String lawyerId) {
        AddContactoFragment fragment = new AddContactoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTACT_ID, lawyerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAccountId=getArguments().getString(ARG_CONTACT_ID);
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_add_contacto, container, false);

        mGenero= (Spinner) root.findViewById(R.id.et_genero);
        mEntidad= (Spinner) root.findViewById(R.id.et_entidad);
        mAcctEncuesta=(TextView) root.findViewById(R.id.et_Encuesta) ;
        mAcctEncuesta.setText(DataUpload.USER_ACCOUNT_ENC_NAME);
        mNombre=(TextInputEditText) root.findViewById(R.id.et_nombre) ;
        mPaterno=(TextInputEditText) root.findViewById(R.id.et_paterno) ;
        mMaterno=(TextInputEditText) root.findViewById(R.id.et_materno) ;
        mCalle=(TextInputEditText) root.findViewById(R.id.et_calle) ;
        mNColinia=(TextInputEditText) root.findViewById(R.id.et_colonia) ;
        mNumExt=(TextInputEditText) root.findViewById(R.id.et_numext) ;
        mNumInt=(TextInputEditText) root.findViewById(R.id.et_numint) ;
        mNumCodPostal=(TextInputEditText) root.findViewById(R.id.et_codpostal) ;
        mMunicipio=(TextInputEditText) root.findViewById(R.id.et_municipio) ;
        mFecNac=(TextView) root.findViewById(R.id.et_fecnac);
        mSelectFech=(Button) root.findViewById(R.id.btnSelFech);

        mSaveButton= (FloatingActionButton) getActivity().findViewById(R.id.fabadd) ;

        spinnerArray = new ArrayList<String>();
        spinnerEstados = new ArrayList<String>();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.genero, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> est = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.estados, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        est.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mGenero.setAdapter(adapter);
        mEntidad.setAdapter(est);

        mSelectFech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataUpload.UPDATE_PREGUNTAS_COMPONENT=false;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DialogFragment newFragment = new TimeSet();
                newFragment.show(ft, "datePicker");
            }
        });
        final Calendar c= Calendar.getInstance();
        mYear= c.get(Calendar.YEAR);
        mMes= c.get(Calendar.MONTH);
        mDay= c.get(Calendar.DAY_OF_MONTH);
        // Instancia de helper
        dbsEncuesta = new DataSesion(getActivity());
        updateDisplay();
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewContact();
            }
        });
        return root;

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
        mFecNacText=new StringBuilder()
                .append(mYear)
                .append(sn)
                .append(kn).toString();
        mFecNac.setText(
                new StringBuilder()
                        .append(kn).append("/")
                        .append(sn).append("/")
                        .append(mYear).append(" "));
    }


    private void AddNewContact(){

        boolean auth=false;
        String nombre= mNombre.getText().toString();
        String paterno= mPaterno.getText().toString();
        String materno= mMaterno.getText().toString();
        String calle= mCalle.getText().toString();
        String colonia= mNColinia.getText().toString();
        String numext= mNumExt.getText().toString();
        String numint= mNumInt.getText().toString();
        String codpostal= mNumCodPostal.getText().toString();
        String municipio= mMunicipio.getText().toString();

        if(TextUtils.isEmpty(nombre)){
            mNombre.setError("Obligatorio");auth=true;
        }if(TextUtils.isEmpty(paterno)){
            mPaterno.setError("Obligatorio");auth=true;
        }if(TextUtils.isEmpty(materno)){
            mMaterno.setError("Obligatorio");auth=true;
        }if(TextUtils.isEmpty(calle)){
            mCalle.setError("Obligatorio");auth=true;
        }if(TextUtils.isEmpty(colonia)){
            mNColinia.setError("Obligatorio");auth=true;
        }if(TextUtils.isEmpty(numext)){
            mNumExt.setError("Obligatorio");auth=true;
        }if(TextUtils.isEmpty(numint)){
            mNumInt.setError("Obligatorio");auth=true;
        }if(TextUtils.isEmpty(codpostal)){
            mNumCodPostal.setError("Obligatorio");auth=true;
        }if(TextUtils.isEmpty(municipio)){
            mMunicipio.setError("Obligatorio");auth=true;
        }
        if(auth){
            return;
        }
        //Log.v("Salect : ",String.valueOf(mAcctEncuesta.getSelectedItemId()));
        int n=Integer.parseInt(DataUpload.USER_ACCOUNT_ENC_ID);
        DataUpload.USER_NEW_ACCOUNT_ENC_ID=n;
        //Log.v("Salect : ",String.valueOf(n));
        DataUpload.USER_ID_CONTACT= UUID.randomUUID().toString();
        tContactos con= new tContactos(0,n,Integer.parseInt(DataUpload.USER_ID), mNombre.getText().toString(), mPaterno.getText().toString(),
               mMaterno.getText().toString(), mCalle.getText().toString(), mNColinia.getText().toString(),
               mNumExt.getText().toString(), mNumInt.getText().toString(),
              Integer.parseInt(mNumCodPostal.getText().toString()), String.valueOf(mEntidad.getSelectedItemPosition()+1),
              mMunicipio.getText().toString(), mFecNacText.toString(),
                String.valueOf(mGenero.getSelectedItemPosition()+1));
        new SaveTaskUser().execute(con);

    }

    private class SaveTaskUser extends AsyncTask<tContactos, Void, Boolean> {
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
        protected Boolean doInBackground(tContactos... contacts) {
            //if(mAccountId!=null){
            //} else {
                return dbsEncuesta.InsertContacto(contacts[0])>0;
            //}return null;
        }
        @Override
        protected void onPostExecute(Boolean res) {
            if (pd != null) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                pd = null;
            }
            showScreen(res);
        }
    }
    private void showScreen(Boolean b){
        if(!b){
            getActivity().setResult(Activity.RESULT_CANCELED);
        }else {
            getActivity().setResult(Activity.RESULT_OK);
        }
        ///ABRIR EL CUESTIONARIO CORRESPONDIENTE
        getActivity().finish();
    }
}

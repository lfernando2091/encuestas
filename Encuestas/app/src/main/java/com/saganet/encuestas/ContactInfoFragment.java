package com.saganet.encuestas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ContactInfoFragment extends DialogFragment  {

    public static final int REQUEST_INPUT_TEST = 7;
    private TextView mIdContactoUs;
    private TextView mIdPrecargaUs;
    private TextView mNombreEncuestaUs;
    private TextView mNombreUs;
    private TextView mCalleUs;
    private TextView mColoniaUs;
    private TextView mNumExtUs;
    private TextView mNumIntUs;
    private TextView mCodPostalUs;
    private TextView mEntidadUs;
    private TextView mMunicipioUs;
    private TextView mFechNacUs;
    private TextView mGeneroUs;

    public static String sIdContactoUs;
    public static String sIdPrecargaUs;
    public static String sNombreEncuestaUs;
    public static String sNombreUs;
    public static String sCalleUs;
    public static String sColoniaUs;
    public static String sNumExtUs;
    public static String sNumIntUs;
    public static String sCodPostalUs;
    public static String sEntidadUs;
    public static String sMunicipioUs;
    public static String sFechNacUs;
    public static String sGeneroUs;

    private Button mButtonCancel;
    private Button mButtonOk;
    private Button mButtonTag;
    public static final String RESULT_OK="OK";
    public static final String RESULT_CANCELED="CANCELED";
    public static final String RESULT_TAG="TAGSET";
    public static final String RESULT_TYPE="ListResult";

    public ContactInfoFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }
    public static ContactInfoFragment newInstance(String title) {
        ContactInfoFragment frag = new ContactInfoFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_info, container);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mIdContactoUs = (TextView) view.findViewById(R.id.t_idcontacto_c);
        mIdPrecargaUs = (TextView) view.findViewById(R.id.t_idprecarga_c);
        mNombreEncuestaUs = (TextView) view.findViewById(R.id.t_nomencuesta_c);
        mNombreUs = (TextView) view.findViewById(R.id.t_nombreus_c);
        mCalleUs = (TextView) view.findViewById(R.id.t_calleus_c);
        mColoniaUs = (TextView) view.findViewById(R.id.t_coloniaus_c);
        mNumExtUs = (TextView) view.findViewById(R.id.t_munext_c);
        mNumIntUs = (TextView) view.findViewById(R.id.t_numintus_c);
        mCodPostalUs = (TextView) view.findViewById(R.id.t_codpostus_c);
        mEntidadUs = (TextView) view.findViewById(R.id.t_entidadus_c);
        mMunicipioUs = (TextView) view.findViewById(R.id.t_municipious_c);
        mFechNacUs = (TextView) view.findViewById(R.id.t_fechnacus_c);
        mGeneroUs = (TextView) view.findViewById(R.id.t_genus_c);

        mButtonCancel = (Button) view.findViewById(R.id.b_cancel_c);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFinishAsk(false);
            }
        });
        mButtonOk = (Button) view.findViewById(R.id.b_ok_c);
        if(DataUpload.IS_REDITABLE_ENCUESTA_ID){
            mButtonOk.setText("CONTINUAR ENCUESTA");
        }else {
            mButtonOk.setText("INICIAR ENCUESTA");
        }
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFinishAsk(true);
            }
        });
        mButtonTag = (Button) view.findViewById(R.id.b_tag_c);
        mButtonTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DataUpload.IS_REDITABLE_ENCUESTA_ID){
                    showFinishTag();
                }
            }
        });
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        mIdContactoUs.setText(sIdContactoUs);
        mIdPrecargaUs.setText(sIdPrecargaUs);
        mNombreEncuestaUs.setText(sNombreEncuestaUs);
        mNombreUs.setText(sNombreUs);
        mCalleUs.setText(sCalleUs);
        mColoniaUs.setText(sColoniaUs);
        mNumExtUs.setText(sNumExtUs);
        mNumIntUs.setText(sNumIntUs);
        mCodPostalUs.setText(sCodPostalUs);
        mEntidadUs.setText(sEntidadUs);
        mMunicipioUs.setText(sMunicipioUs);
        mFechNacUs.setText(sFechNacUs);
        mGeneroUs.setText(sGeneroUs.equals("1")?"Hombre": sGeneroUs.equals("2")?"Mujer":"Desconocido");
        //getDialog().getWindow().setSoftInputMode(
         //       WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
    private void showFinishAsk(boolean q){
        //getActivity().setResult(q? Activity.RESULT_OK: Activity.RESULT_CANCELED);
        Intent intent = new Intent();
        intent.putExtra(RESULT_TYPE, q? RESULT_OK: RESULT_CANCELED);
        getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_INPUT_TEST, intent);
        getDialog().dismiss();
    }
    private void showFinishTag(){
        Intent intent = new Intent();
        intent.putExtra(RESULT_TYPE,RESULT_TAG);
        getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_INPUT_TEST, intent);
        getDialog().dismiss();
    }

}

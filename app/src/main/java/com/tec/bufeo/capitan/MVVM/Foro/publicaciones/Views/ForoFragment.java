package com.tec.bufeo.capitan.MVVM.Foro.publicaciones.Views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.tec.bufeo.capitan.Activity.DetalleFotoUsuario;
import com.tec.bufeo.capitan.Activity.DetalleNegocio;
import com.tec.bufeo.capitan.Activity.DetallesTorneo.DetalleTorneoNuevo;
import com.tec.bufeo.capitan.Activity.PerfilUsuarios.PublicacionesUsuario.PerfilUsuarios;
import com.tec.bufeo.capitan.Activity.ProfileActivity;
import com.tec.bufeo.capitan.Activity.RegistroForo;
import com.tec.bufeo.capitan.MVVM.Foro.Versus.Views.FragmentVersus;
import com.tec.bufeo.capitan.MVVM.Foro.publicaciones.Models.ModelFeed;
import com.tec.bufeo.capitan.MVVM.Foro.publicaciones.Repository.FeedRoomDBRepository;
import com.tec.bufeo.capitan.MVVM.Foro.publicaciones.Repository.FeedWebServiceRepository;
import com.tec.bufeo.capitan.MVVM.Foro.publicaciones.ViewModels.FeedListViewModel;
import com.tec.bufeo.capitan.R;
import com.tec.bufeo.capitan.Util.GlideCache.IntegerVersionSignature;
import com.tec.bufeo.capitan.Util.Preferences;
import com.tec.bufeo.capitan.Util.UniversalImageLoader;
import com.tec.bufeo.capitan.WebService.DataConnection;
import com.tec.bufeo.capitan.WebService.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.tec.bufeo.capitan.Util.GlideCache.IntegerVersionSignature.GlideOptions.LOGO_OPTION;
import static com.tec.bufeo.capitan.WebService.DataConnection.IP2;


public class ForoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    Preferences preferences;
    RecyclerView rcv_foro;
    FloatingActionButton fab_registrarForo;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    ImageView partidos;
    AdaptadorForo adapter = null;
    FeedListViewModel feedListViewModel;
    Activity activity;
    static Context context;
    View view = null;
    ImageView fotoPerfil;
    EditText floating_search_view;
    FrameLayout FrameNuevosDatos;
    LinearLayout layout_nuevosDatos;
    View bottomShet;
    BottomSheetBehavior mBottomSheetBehavior;
    LinearLayout tap_de_accion,bottomDelete;
    ImageView btnClose;
    TextView titulotap,saldo_contable;
    UniversalImageLoader universalImageLoader;
    String idpublicacion;
    DataConnection dc;


    public ForoFragment() {
    }


    public static boolean isOnLine(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return  true;
        }else {
            return false;
        }

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //    progressDialog = new ProgressDialog(getActivity());
        feedListViewModel = ViewModelProviders.of(getActivity()).get(FeedListViewModel.class);
        //commentsListViewModel =  ViewModelProviders.of(getActivity()).get(VersusListViewModel.class);
    }

    public void obtenerSaldo(){
        dc = new DataConnection(activity, "ObtenerSaldo", false);
        new GetSaldo().execute();
    }
    ArrayList<String> saldo = new ArrayList<>();
    public class GetSaldo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            saldo = new ArrayList<>();
            saldo = dc.getSaldo();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            if (saldo.size() > 0) {
                saldo_contable.setText(saldo.get(0));
            } else {
                saldo_contable.setText("vacio");
            }


        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_foro, container, false);
        context = getContext();
        activity = getActivity();
        preferences = new Preferences(context);




        initViews(view);
        obtenerSaldo();
        setAdapter();
        cargarvista();


        if (preferences.getCantidadIngreso().equals("1")){
            feed();
        }
        return view;
    }



    

    @Override
    public void onResume() {

        super.onResume();
        //onRefresh();

    }

    private void initViews(View view){


        bottomShet = view.findViewById(R.id.bottomShet);
        tap_de_accion = view.findViewById(R.id.tap_de_accion);
        titulotap = view.findViewById(R.id.titulotap);
        bottomDelete = view.findViewById(R.id.bottomDelete);
        btnClose = view.findViewById(R.id.btnClose);
        saldo_contable = view.findViewById(R.id.saldo_contable);


        fotoPerfil = view.findViewById(R.id.fotoPerfil);
        partidos = view.findViewById(R.id.partidos);
        rcv_foro = (RecyclerView) view.findViewById(R.id.rcv_foro);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        FrameNuevosDatos =  view.findViewById(R.id.FrameNuevosDatos);
        floating_search_view =  view.findViewById(R.id.floating_search_view);
        swipeRefreshLayout =  view.findViewById(R.id.SwipeRefreshLayout);
        layout_nuevosDatos =  view.findViewById(R.id.layout_nuevosDatos);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        fab_registrarForo = view.findViewById(R.id.fab_registrarForo);

        fab_registrarForo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), RegistroForo.class);
                i.putExtra("concepto","publicacion");
                startActivity(i);
            }
        });

        btnClose.setOnClickListener(this);
        bottomDelete.setOnClickListener(this);
        FrameNuevosDatos.setOnClickListener(this);
        partidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });


        Glide.with(context)
                .load(IP2+"/"+ preferences.getFotoUsuario())
                .signature(new IntegerVersionSignature(preferences.getCantidadFotoPerfil()))
                .apply(LOGO_OPTION)
                .into(fotoPerfil);






        floating_search_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String v = String.valueOf(s);
                buscar(v);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(context, ProfileActivity.class);

                i.putExtra("id_usuario",preferences.getIdUsuarioPref());
                startActivity(i);
            }
        });

        rcv_foro.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab_registrarForo.isShown())
                    fab_registrarForo.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab_registrarForo.show();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //feed();


        mBottomSheetBehavior= BottomSheetBehavior.from(bottomShet);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tap_de_accion.setVisibility(View.GONE);
                }
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tap_de_accion.setVisibility(View.VISIBLE);
                }
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    tap_de_accion.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        layout_nuevosDatos.setOnClickListener(this);


    }








    private void showEditDialog() {
        FragmentManager fm = getFragmentManager();
        FragmentVersus editNameDialogFragment = FragmentVersus.newInstance("Some Title");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }


    public void buscar(String s){
        feedListViewModel.getSearh(s).observe(this, new Observer<List<ModelFeed>>() {
            @Override
            public void onChanged(List<ModelFeed> modelFeeds) {
                adapter.setWords(modelFeeds);

            }
        });


    }


    String valorNuevo;
    public void cargarvista(){

        feedListViewModel.getAllPosts().observe(getViewLifecycleOwner(), new Observer<List<ModelFeed>>() {
            @Override
            public void onChanged(List<ModelFeed> modelFeeds) {
                if (modelFeeds.size()>0){

                    adapter.setWords(modelFeeds);
                    valorNuevo = modelFeeds.get(0).getNuevos_datos();
                    if (valorNuevo!=null){
                        if (valorNuevo.equals("1")){
                            FrameNuevosDatos.setVisibility(View.VISIBLE);
                        }else{
                            FrameNuevosDatos.setVisibility(View.GONE);
                        }
                    }

                    limite_sup = modelFeeds.get(modelFeeds.size()-1).getLimite_sup();
                    limite_inf = modelFeeds.get(modelFeeds.size()-1).getLimite_inf();

                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    //Toast.makeText(getContext(), "sup: " + limite_sup + " inf: " + limite_inf, Toast.LENGTH_SHORT).show();
                }else{
                    limite_sup= "0";
                    limite_inf= "0";
                }

                preferences.saveValuePORT("lim_sup",limite_sup);
                preferences.saveValuePORT("lim_inf",limite_inf);
            }
        });

    }



    private void setAdapter(){

        adapter= new AdaptadorForo(getActivity(), new AdaptadorForo.OnItemClickListener() {
            @Override
            public void onItemClick(String dato, ModelFeed feedTorneo, int position) {
               if (dato.equals("btnAccion")){

                    idpublicacion=feedTorneo.getPublicacion_id();
                    fab_registrarForo.hide();
                    if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
               if (dato.equals("foto_perfil_publicacion")){
                   if (feedTorneo.getId_torneo().equals("0")){
                       if(feedTorneo.getUsuario_id().equals(preferences.getIdUsuarioPref())){
                           Intent i = new Intent(getContext(), ProfileActivity.class);
                           startActivity(i);
                       }else{
                           Intent i = new Intent(getContext(), PerfilUsuarios.class);
                           i.putExtra("id_user",feedTorneo.getUsuario_id());
                       startActivity(i);
                   }
                   }else{
                       Intent i = new Intent(getContext(), DetalleTorneoNuevo.class);
                       i.putExtra("id_torneo",feedTorneo.getId_torneo());
                       i.putExtra("foto",feedTorneo.getTorneo_foto());
                       i.putExtra("nombre",feedTorneo.getPublicacion_torneo());
                       i.putExtra("id_usuario",feedTorneo.getUsuario_id());
                       startActivity(i);
                   }

                }if (dato.equals("txt_usuarioForo")){
                    if (feedTorneo.getId_torneo().equals("0")){
                        if(feedTorneo.getUsuario_id().equals(preferences.getIdUsuarioPref())){
                            Intent i = new Intent(getContext(), ProfileActivity.class);
                            startActivity(i);
                        }else{
                            Intent i = new Intent(getContext(), PerfilUsuarios.class);
                            i.putExtra("id_user",feedTorneo.getUsuario_id());
                            startActivity(i);
                        }
                    }else{
                        Intent i = new Intent(getContext(), DetalleTorneoNuevo.class);
                        i.putExtra("id_torneo",feedTorneo.getId_torneo());
                        i.putExtra("foto",feedTorneo.getTorneo_foto());
                        i.putExtra("nombre",feedTorneo.getPublicacion_torneo());
                        i.putExtra("id_usuario",feedTorneo.getUsuario_id());
                        startActivity(i);
                    }


                }if (dato.equals("img_fotoForo")){
                    Intent i = new Intent(getContext(), DetalleFotoUsuario.class);
                    i.putExtra("foto",feedTorneo.getForo_foto());
                    i.putExtra("descripcion",feedTorneo.getForo_descripcion());
                    i.putExtra("cantidad_comentarios",feedTorneo.getCant_Comentarios());
                    i.putExtra("id_publicacion",feedTorneo.getPublicacion_id());
                    startActivity(i);
                }if(dato.equals("pedir")){
                   //feed();
                   preferences.codeAdvertencia(String.valueOf(position));
                }else if(dato.equals("verMasTorneo")){
                    Intent i = new Intent(getContext(), DetalleTorneoNuevo.class);
                    i.putExtra("id_torneo",feedTorneo.getId_torneo());
                    i.putExtra("foto",feedTorneo.getTorneo_foto());
                    i.putExtra("nombre",feedTorneo.getPublicacion_torneo());
                    i.putExtra("id_usuario",feedTorneo.getUsuario_id());
                    startActivity(i);
                }else if (dato.equals("imgbt_like")){
                    if (feedTorneo.getDio_like().equals("0")){
                        darlike(feedTorneo.getPublicacion_id());
                    }else{
                        dislike(feedTorneo.getPublicacion_id());
                    }
                }
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        rcv_foro.setAdapter(adapter);
        rcv_foro.setLayoutManager(layoutManager);



    }

    JSONObject json_data;
    String resultado;
    int totalLikes;
    private void darlike(final String idlike) {
        String url =IP2+"/api/Foro/dar_like";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("darlike: ",""+response);

                try {
                    json_data = new JSONObject(response);
                    JSONArray resultJSON = json_data.getJSONArray("results");
                    JSONObject jsonNodev = resultJSON.getJSONObject(0);
                    resultado = jsonNodev.optString("resultado");
                    totalLikes = Integer.parseInt(jsonNodev.optString("likes"));

                    if (resultado.equals("1")){

                        FeedRoomDBRepository feedRoomDBRepository = new FeedRoomDBRepository(application);
                        feedRoomDBRepository.darlike("1");
                        feedRoomDBRepository.cantidadLikes(String.valueOf(totalLikes));
                    }






                } catch (JSONException e) {
                    e.printStackTrace();
                }





                //Toast.makeText(ChoferDatosDeCarrera.this,"No se ha registrado ",Toast.LENGTH_SHORT).show();


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context,"error ",Toast.LENGTH_SHORT).show();
                Log.i("RESPUESTA: ",""+error.toString());

            }
        })  {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //String imagen=convertirImgString(bitmap);


                Map<String,String> parametros=new HashMap<>();
                parametros.put("usuario_id",preferences.getIdUsuarioPref());
                parametros.put("publicacion_id",idlike);
                parametros.put("app","true");
                parametros.put("token",preferences.getToken());

                return parametros;

            }
        };
        /*requestQueue.add(stringRequest);*/
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(context).addToRequestQueue(stringRequest);
    }


    private void dislike(final String iddislike) {
        String url =IP2+"/api/Foro/quitar_like";
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("dislike: ",""+response);

                try {
                    json_data = new JSONObject(response);
                    JSONArray resultJSON = json_data.getJSONArray("results");
                    JSONObject jsonNodev = resultJSON.getJSONObject(0);
                    resultado = jsonNodev.optString("resultado");
                    totalLikes = Integer.parseInt(jsonNodev.optString("likes"));

                    if (resultado.equals("1")){

                        FeedRoomDBRepository feedRoomDBRepository = new FeedRoomDBRepository(application);
                        feedRoomDBRepository.dislike("0");
                        feedRoomDBRepository.cantidadLikes(String.valueOf(totalLikes));
                    }






                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context,"error ",Toast.LENGTH_SHORT).show();
                Log.i("RESPUESTA: ",""+error.toString());

            }
        })  {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //String imagen=convertirImgString(bitmap);


                Map<String,String> parametros=new HashMap<>();
                parametros.put("usuario_id",preferences.getIdUsuarioPref());
                parametros.put("publicacion_id",iddislike);
                parametros.put("app","true");
                parametros.put("token",preferences.getToken());

                return parametros;

            }
        };
        //requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(context).addToRequestQueue(stringRequest);
    }
    Application application;
    @Override
    public void onRefresh() {

        //feedListViewModel.deleteAllFeed();
        FeedWebServiceRepository feedTorneoWebServiceRepository = new FeedWebServiceRepository(application);
        feedTorneoWebServiceRepository.providesWebService(preferences.getIdUsuarioPref(),"0","0",preferences.getToken());
        /*setAdapter();
        cargarvista();*/
        Log.e("prueba", "onRefresh: funciona" );
        swipeRefreshLayout.setRefreshing(false);
    }

    String limite_sup,limite_inf,superior_envio,inferior_envio;
    public void feed(){

        if (preferences.getLimite_sup().equals("")|| preferences.getLimite_sup()==null){
            superior_envio = "0";
            inferior_envio = "0";
        }else {
            superior_envio = preferences.getLimite_sup();
            inferior_envio = preferences.getLimite_inf();
        }
        //preferences.toasVerde("ok");

        FeedWebServiceRepository feedTorneoWebServiceRepository = new FeedWebServiceRepository(application);
        feedTorneoWebServiceRepository.providesWebService(preferences.getIdUsuarioPref(),superior_envio,inferior_envio,preferences.getToken());
    }


    @Override
    public void onClick(View v) {
        if (v.equals(btnClose)){
            fab_registrarForo.show();
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }else if(v.equals(bottomDelete)){
            dialogoEliminarPublicacion();
        }else if (v.equals(FrameNuevosDatos)){
            onRefresh();
            FrameNuevosDatos.setVisibility(View.GONE);
        }else if (v.equals(layout_nuevosDatos)){
            feed();
        }
    }

    //Dialog dialogr;
    AlertDialog dialog_eliminar;
    LinearLayout fondoDialogo;
    public void dialogoEliminarPublicacion(){

        AlertDialog.Builder builder =  new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialogo_eliminar,null);
        builder.setView(vista);

        dialog_eliminar = builder.create();
        dialog_eliminar.show();




        fondoDialogo = vista.findViewById(R.id.fondoDialogo);
        MaterialButton btn_cancela = vista.findViewById(R.id.btn_cancelar);
        MaterialButton btn_acepta =  vista.findViewById(R.id.btn_eliminar);


        fondoDialogo.getBackground().setAlpha(150);
        btn_cancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_eliminar.dismiss();
            }
        });

        btn_acepta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fondoDialogo.setVisibility(View.VISIBLE);
                EliminarPublicacionWeb(idpublicacion);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });
    }

    StringRequest stringRequest;
    private void EliminarPublicacionWeb(final String publicacion_id) {
        String url =IP2+"/api/Foro/eliminar_publicacion";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("foro","respuesta al eliminar publicacion "+response);

                if (response.toString().equals("1")){
                    Toast.makeText(context, "Publicacion eliminada", Toast.LENGTH_SHORT).show();
                    //retroViewModel.deleteAllFeed();
                    //onRefresh();
                    feedListViewModel.deleteOneFeed(publicacion_id);
                    dialog_eliminar.dismiss();
                }
            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context,"error ",Toast.LENGTH_SHORT).show();
                Log.i("foro: ","respuesta error eliminar publicacion "+error.toString());

            }
        })  {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //String imagen=convertirImgString(bitmap);


                Map<String,String> parametros=new HashMap<>();
                parametros.put("publicacion_id",publicacion_id);
                parametros.put("app","true");
                parametros.put("token",preferences.getToken());
                Log.i("foro: ","parametros "+parametros.toString());
                return parametros;

            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getIntanciaVolley(context).addToRequestQueue(stringRequest);
    }
}

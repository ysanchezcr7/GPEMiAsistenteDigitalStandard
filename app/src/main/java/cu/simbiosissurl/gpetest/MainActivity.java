package cu.simbiosissurl.gpetest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    WebView webView;
    // ProgressBar progressBar;
    ProgressBar progress;
    //  String FileName = "mi-acistente-digital.txt";

    private static final String APP_CACAHE_DIRNAME = "/gpeCache";
    //private int REQUEST_CODE = 1234;
    DownloadBlobFileJSInterface mDownloadBlobFileJSInterface;
    // Button btnRetry;


    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    Handler handler = new Handler(); // En esta zona creamos el objeto Handler


    private String URLPrincipal = "https://gpestandard.simbiosis-dg-apps.com";
    //private String URLSaltoTutorial = "https://gpetest.simbiosis-dg-apps.com/menu.html";
    int detectError = 0;
    CircleImageView floatingActionButton;
    private BroadcastReceiver updateRecirver = null;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = findViewById(R.id.progress_appr);
        floatingActionButton = findViewById(R.id.floating);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (webView.canGoBack()) {// ¿Hay una página anterior cuando hago clic en el botón Atrás

                    if (webView.getUrl().equals("file:///android_asset/error404.html") && detectError == 1) {
                        detectError = 0;
                        showAlertSalirApp();
                        //finish();

                    } else {
                        webView.loadUrl("https://gpetest.simbiosis-dg-apps.com/menu.html");

                        //webView.goBack();// goBack () significa volver a la página anterior de webView
                    }
                } else {
                    showAlertSalirApp();
                    //finish();
                }
                NoVisibleFloating();

            }
        });
        webView = findViewById(R.id.webView);

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        //WebSettings webSettings = webView.getSettings();
        WebSettings settings = webView.getSettings();

        if (isNetworkAvailable()) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webView.getSettings().setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        // webView.getSettings (). setBlockNetworkImage (true); // coloca la carga de la imagen al final
        // para cargar la representación
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

//        File dir = getCacheDir();
//        Log.e("sada",""+ dir);


// almacenamiento (almacenamiento)
        WebSettings websettings = webView.getSettings();
        websettings.setDomStorageEnabled(true);  // Open DOM storage function
        websettings.setAppCacheMaxSize(1024*1024*8);
        String appCachePath = this.getApplicationContext().getCacheDir().getAbsolutePath();
        websettings.setAppCachePath(appCachePath);
     //   websettings.setAllowFileAccess(true);    // Readable file cache
        websettings.setAppCacheEnabled(true);    //Turn on the H5(APPCache) caching function

// Habilita la API de almacenamiento DOM de HTML5, el valor predeterminado es falso
  //      settings.setDomStorageEnabled(true);
//// Habilite la API de base de datos Web SQL, esta configuración afectará a todas las WebViews en el mismo proceso, el valor predeterminado es falso
//// Esta API está en desuso
        //String cacheDirPath = getApplicationContext().getCacheDir() + APP_CACAHE_DIRNAME;
        //settings.setDatabaseEnabled(true);
//        settings.setAppCacheEnabled(true);
//        File directory=getApplicationContext().getExternalFilesDir("/cachWebView");
//        if (!directory.exists())
//            directory.mkdirs();
//        settings.setDatabasePath(String.valueOf(directory));
//// Para habilitar la API de cachés de aplicaciones, debe establecer una ruta de caché válida para que surta efecto, el valor predeterminado es falso
       // settings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
       // webView.getSettings().setAppCachePath(String.valueOf(directory));
        //settings.setAppCachePath(MainActivity.this.getCacheDir().getAbsolutePath());
   //     settings.setAppCacheMaxSize(Long.MAX_VALUE);

//// ubicación
        settings.setGeolocationEnabled(true);
//// Si guardar los datos del formulario
        settings.setSaveFormData(true);
//// Si establecer el foco para un elemento de la página cuando webview llama a requestFocus, el valor predeterminado es verdadero
//        settings.setNeedInitialFocus(true);
//// Si se admite el atributo de ventana gráfica, el valor predeterminado es falso
//// La página pasa por `<meta name =" viewport "... />` pantalla adaptable del teléfono
        settings.setUseWideViewPort(true);
//// Si se usa el modo de vista general para cargar la página, el valor predeterminado es falso
//// Cuando el ancho de la página es mayor que el ancho de WebView, reduzca para que el ancho de la página sea igual al ancho de WebView
        settings.setLoadWithOverviewMode(true);
//// Algoritmo de diseño
       settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//// Si es compatible con Javascript, el valor predeterminado es falso
        settings.setJavaScriptEnabled(true);
//// Si es compatible con múltiples ventanas, el valor predeterminado es falso
//        settings.setSupportMultipleWindows(true);
//// Si se usa Javascript (window.open) para abrir la ventana, el valor predeterminado es falso
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//// Acceso a recursos
        settings.setAllowContentAccess(true); // Ya sea para acceder a los recursos del Proveedor de contenido, el valor predeterminado es verdadero
        settings.setAllowFileAccess(true);    // Si se puede acceder al archivo local, el valor predeterminado es verdadero
//// Ya sea para permitir que Javascript cargado a través de la URL del archivo lea archivos locales, el valor predeterminado es falso
        settings.setAllowFileAccessFromFileURLs(true);
//// Ya sea para permitir que Javascript cargado a través de la URL del archivo lea todos los recursos (incluidos los archivos, http, https), el valor predeterminado es falso
        settings.setAllowUniversalAccessFromFileURLs(true);
//// carga de recursos
        settings.setLoadsImagesAutomatically(true); // Si cargar automáticamente la imagen
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }
        settings.setBlockNetworkImage(false);       // Prohibir cargar imágenes de red
        settings.setBlockNetworkLoads(false);       // Prohibir cargar todos los recursos de la red
//// Cerca cerca)
        settings.setSupportZoom(true);          // Ya sea para soportar zoom
        settings.setBuiltInZoomControls(false); // Si se usa el mecanismo de zoom incorporado
        settings.setDisplayZoomControls(false);  // Si se muestra el control de zoom incorporado
//// Codificación de texto predeterminada, valor predeterminado "UTF-8"
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setDefaultFontSize(16);        // El tamaño de texto predeterminado, el valor predeterminado es 16, el rango de valores es 1-72
        settings.setDefaultFixedFontSize(16);   // El tamaño de fuente monoespacio predeterminado, el valor predeterminado es 16
        settings.setMinimumFontSize(8);         // El tamaño mínimo del texto, el valor predeterminado es 8
        settings.setMinimumLogicalFontSize(8);  // El tamaño mínimo del texto, el valor predeterminado es 8
        settings.setTextZoom(100);              // Porcentaje de zoom de texto, el valor predeterminado es 100


// 2. Add JavascriptInterface
        mDownloadBlobFileJSInterface = new DownloadBlobFileJSInterface(this);
        webView.addJavascriptInterface(mDownloadBlobFileJSInterface, "Android");
        // webView.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface"); // To call methods in Android from using js in the html, AndroidInterface.showToast, AndroidInterface.getAndroidVersion etc

        // 3. Execute JS code
        //webView.loadUrl(DownloadBlobFileJSInterface.getBase64StringFromBlobUrl(URL));

        //para asegurar que el webView sea cargado satisfactoriamente
        webView.setWebChromeClient(new MyWebChromeClient());
        //webView.setWebViewClient(new MyWebViewClient());
        webView.setWebViewClient(new MyWebViewClient());
        //File appCacheDir = new File(cacheDirPath);
//        SharedPreferences miPreferenciaUrl = getSharedPreferences("UrlTutorial", Context.MODE_PRIVATE);
//        String urlPreferencia = miPreferenciaUrl.getString("url", "null");
//        if (urlPreferencia.equals("null")) {
//            if (isNetworkAvailable()) {
//                SharedPreferences.Editor editor = miPreferenciaUrl.edit();
//                editor.putString("url", URLPrincipal);
//                editor.commit();
//                detectError = 0;
//            }
//            webView.loadUrl(URLPrincipal);
//        } else {
//            webView.loadUrl(URLSaltoTutorial);
//        }
        webView.loadUrl(URLPrincipal);
        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {

//                Log.e("userAgent",""+ userAgent);
//                Log.e("contentDisposition",""+ contentDisposition);
//                Log.e("mimetype",""+mimetype);
//                Log.e("contentLength",""+contentLength);

                if (url.startsWith("blob:")) {
                    webView.loadUrl(DownloadBlobFileJSInterface.getBase64StringFromBlobUrl(url));
                } else {
                    Log.e("contentLength", "" + contentLength);
                    //DownloadTask task = new DownloadTask();
                    //task.execute(url);
                }

            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            ejecutarTareaPermisos();
        }

        updateRecirver = new UpdateRecirver();
        broadcastIntent();
    }

    public class UpdateRecirver extends BroadcastReceiver {

        Context context;


        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
            if (isConnected) {
                // Toast.makeText(context, "Conectado "+ isConnected,
                //        Toast.LENGTH_LONG).show();
                Log.i("NET", "Not Connected" + isConnected);


            } else {
                // Create the Snackbar
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar.make(parentLayout, R.string.no_internet_conection, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Cerrar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });


// Get the Snackbar's layout view
                Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
//If the view is not covering the whole snackbar layout, add this line
                layout.setPadding(1, 1, 1, 1);

// Show the Snackbar
                snackbar.show();
//            Toast.makeText(context, "¡Parece que no estás conectado a Internet!. Pueden existir páginas " +
//                            "no guardadas en cache que necesiten de conexion a intennet ",
//                    Toast.LENGTH_LONG).show();
//            Log.i("NET", "Not Connected" + isConnected);

            }


        }
    }

    public void broadcastIntent() {
        registerReceiver(updateRecirver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    private final int TIEMPO = 5000;

    public void ejecutarTareaPermisos() {
        handler.postDelayed(new Runnable() {
            public void run() {
                // función a ejecutar
                // actualizarChofer(); // función para refrescar la ubicación del conductor, creada en otra línea de código
                initPermission();
                //handler.postDelayed(this, TIEMPO);
            }

        }, TIEMPO);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    class MyWebViewClient extends WebViewClient {


        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
            //Toast.makeText(getApplicationContext(), "LOLLIO ERROR" + webResourceRequest.getUrl().toString(), Toast.LENGTH_LONG).show();
//            //view.loadUrl("about: blank"); // Evita la interfaz de error predeterminada
            // Log.e("Tag", "LOLLIO ERROR" + webResourceRequest.getUrl().toString());
            String urlE = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                urlE = webResourceRequest.getUrl().toString();
            }
            if (urlE.equals("https://gpetest.simbiosis-dg-apps.com/")) {
                detectError = 1;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (detectError == 1) {// ¿Se crea para el marco principal?
                    if (webResourceRequest.isForMainFrame()) {
                        try {
                            view.stopLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            view.clearView();
                            // view.loadUrl("about:blank");
                            //String ErrorPagePath = "file:///android_asset/error404.html";
                            //view.loadUrl(ErrorPagePath);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (view.canGoBack()) {
                            view.goBack();
                        }
                        // view.loadUrl("about:blank");
                        //showAlertConexion();
                        // view.loadUrl("about:blank"); // Evita la interfaz de error predeterminada
                        view.loadUrl("file:///android_asset/error404.html");// Cargar una página de error personalizada
                        VisibleFloating();

                    }

                } else {
                    if (webResourceRequest.isRedirect()) {
                        try {
                            view.stopLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            view.clearView();
                            // view.loadUrl("about:blank");
                            //String ErrorPagePath = "file:///android_asset/error404.html";
                            //view.loadUrl(ErrorPagePath);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (view.canGoBack()) {
                            view.goBack();
                        }
                        // view.loadUrl("about:blank");
                        //showAlertConexion();
                        // view.loadUrl("about:blank"); // Evita la interfaz de error predeterminada
                        view.loadUrl("file:///android_asset/error404.html");// Cargar una página de error personalizada
                        VisibleFloating();
                    }
                }

            }
            onReceivedError(view, webResourceError.getErrorCode(), webResourceError.getDescription().toString(), webResourceRequest.getUrl().toString());

            // super.onReceivedError(view, webResourceRequest, webResourceError);

        }

        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {

            // webView.loadUrl("file:///android_asset/error404.html");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Toast.makeText(getApplicationContext(), "vercion mayor que " + Build.VERSION.SDK_INT + ">" + Build.VERSION_CODES.M, Toast.LENGTH_LONG).show();
                return;
            }
            // detectError = 1;
            //view.loadUrl ("about: blank"); // Evita la interfaz de error predeterminada

            try {
                view.stopLoading();
            } catch (Exception e) {
                e.printStackTrace();
            }
           /* try {
                view.clearView();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            if (view.canGoBack()) {
                view.goBack();
            }
            // Toast.makeText(getApplicationContext(), "entre a vercion normal " + errorCode, Toast.LENGTH_LONG).show();

            //view.loadUrl("about: blank"); // Evita la interfaz de error predeterminada
            String ErrorPagePath = "file:///android_asset/error404.html";
            view.loadUrl(ErrorPagePath);
            super.onReceivedError(view, errorCode, description, failingUrl);


        }


        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Toast.makeText(getApplicationContext(), "SHOUL override " + view, Toast.LENGTH_LONG).show();

            // webView.loadUrl("file:///android_asset/diseases.html");
            if (url.startsWith("http:") || url.startsWith("https:")) {

                return false;
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            } catch (Exception e) {

                // Toast t = Toast.makeText(getApplicationContext(), "shouldOverrideUrlLoading Exception:" + e,
                //         Toast.LENGTH_LONG);
                //  t.setGravity(Gravity.CENTER, 0, 0);
                // t.show();
                Log.i("TAG", "shouldOverrideUrlLoading Exception:" + e);
                return true;
            }
        }


        // @RequiresApi(Build.VERSION_CODES.N)
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
            }
            if (url.startsWith("http:") || url.startsWith("https:")) {

                return false;

            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            } catch (Exception e) {

                //  Toast t = Toast.makeText(getApplicationContext(), "shouldOverrideUrlLoading Exception:" + e,
                //         Toast.LENGTH_LONG);
                //  t.setGravity(Gravity.CENTER, 0, 0);
                // t.show();
                Log.i("TAG", "shouldOverrideUrlLoading Exception:" + e);
                return true;
            }
            // return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }

        // La página (url) comienza a cargarse
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.i("", "onPageStarted");
            super.onPageStarted(view, url, favicon);

            //showProgress();
        }

        // La página (url) termina de cargarse
        public void onPageFinished(WebView view, String url) {
//            if (mbErrorOccured == false && mbReloadPressed) {
//                hideErrorLayout();
//                mbReloadPressed = false;
//            }

            super.onPageFinished(view, url);
            Log.i("", "onPageFinished");
            // closeProgress();
        }


        // Las decisiones de procesamiento pueden almacenarse en caché para solicitudes posteriores, el comportamiento predeterminado es cancelar la solicitud
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

          /*  SslCertificate serverCertificate = error.getCertificate();

            if (error.hasError(SSL_UNTRUSTED)) {
                // Check if Cert-Domain equals the Uri-Domain
                String certDomain = serverCertificate.getIssuedTo().getCName();
                try {
                    if(certDomain.equals(new URL(error.getUrl()).getHost())) {
                        handler.proceed();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            else {
                super.onReceivedSslError(view, handler, error);
            }*/
            final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setMessage("notification_error_ssl_cert_invalid");
            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }

        // Este método se abandona en API21 y se llama a subprocesos sin interfaz de usuario
// Intercepta solicitudes de recursos y devuelve datos de respuesta. Cuando se devuelve un valor nulo, WebView continuará cargando recursos
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            return null;
        }

        // Este método se agregó a API21 y se invocó en subprocesos sin interfaz de usuario
// Intercepta solicitudes de recursos y devuelve datos, WebView continuará cargando recursos cuando devuelva nulo
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            //Toast.makeText(getApplicationContext(), "shouldInterceptRequest "+request.getUrl().toString(), Toast.LENGTH_LONG).show();

            return shouldInterceptRequest(view, request.getUrl().toString());
        }


        // El recurso se cargará (url)
        public void onLoadResource(WebView view, String url) {
            //Toast.makeText(getApplicationContext(), "onLoadResource "+url, Toast.LENGTH_LONG).show();

        }

        // Esta devolución de llamada se agregó a API23 y solo se usa para la navegación del marco principal
// Notifique a la aplicación que al navegar a la página anterior, el contenido de su WebView ya no se dibujará.
// Esta devolución de llamada se puede usar para determinar qué contenido visible de WebView se puede reciclar de forma segura para garantizar que no se muestre contenido obsoleto
// Se llama lo antes posible para garantizar que WebView.onDraw no dibuje ningún contenido de página anterior y luego dibuje el color de fondo o el nuevo contenido que debe cargarse.
// Se llamará a este método cuando el cuerpo de respuesta HTTP haya comenzado a cargarse y el DOM será visible en el dibujo posterior.
// Esta devolución de llamada se produce al principio de la carga del documento, por lo que sus recursos (css e imágenes) pueden no estar disponibles.
// Si necesita actualizaciones de vista más detalladas, consulte postVisualStateCallback (largo, WebView.VisualStateCallback).
// Tenga en cuenta que todas las condiciones anteriores también admiten postVisualStateCallback (long, WebView.VisualStateCallback)
        public void onPageCommitVisible(WebView view, String url) {
        }


        // Este método se agregó a API23
// Se produjo un error al cargar recursos, lo que generalmente significa que no se pudo acceder al servidor
// Dado que todos los errores de carga de recursos llamarán a este método, este método debe ser lo más lógico posible

        // Este método se agregó a API23
// Recibí un error HTTP (código de estado> = 400) al cargar recursos (iframe, image, js, css, ajax ...)
//        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//           // webView.loadUrl("file:///android_asset/error404.html");
//           // Toast.makeText(getApplicationContext(), "Your Internet Connection May not be active Or " + errorResponse, Toast.LENGTH_LONG).show();
//        }

        // Si se debe volver a enviar el formulario, el valor predeterminado es no reenviar
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            dontResend.sendToTarget();
        }

        // Notifique a la aplicación que la URL actual se puede almacenar en la base de datos, lo que significa que la URL de acceso actual ha entrado en vigencia y está registrada en el núcleo.
// Este método solo se llamará una vez durante el proceso de carga de la página web, y la página web no devolverá esta función cuando vaya y venga.
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        }


        // Este método se agrega a API21 y se llama en el subproceso de la interfaz de usuario
// Manejar la solicitud de certificado de cliente SSL, si es necesario, mostrar una IU para proporcionar CLAVE.
// Hay tres métodos de respuesta: proceder () / cancelar () / ignorar (), el comportamiento predeterminado es cancelar la solicitud
// Si llama a continue () o cancel (), Webview guardará el resultado de la respuesta en la memoria y no volverá a llamar a onReceivedClientCertRequest para el mismo "host: puerto"
// En la mayoría de los casos, puede iniciar una Actividad a través de KeyChain.choosePrivateKeyAlias ​​para que los usuarios elijan la clave privada adecuada
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
            request.cancel();
        }

        // Manejar la solicitud de autenticación HTTP, el comportamiento predeterminado es cancelar la solicitud
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String
                host, String realm) {
            handler.cancel();
        }

        // Notificar a la aplicación que una cuenta autorizada ha iniciado sesión automáticamente
        public void onReceivedLoginRequest(WebView view, String realm, String account, String
                args) {
        }

        // Dale a la aplicación la oportunidad de manejar eventos de pulsación de teclas
// Si devuelve verdadero, WebView no controla el evento; de lo contrario, WebView seguirá procesando y devuelve falso de forma predeterminada
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }

        // Manejar eventos clave no consumidos por WebView
// WebView siempre consume eventos de pulsación de teclas, a menos que sea una pulsación de tecla del sistema o shouldOverrideKeyEvent devuelve verdadero
// Este método se llama de forma asíncrona cuando se distribuye el evento clave
        public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
            super.onUnhandledKeyEvent(view, event);
        }

        // Notificar a la aplicación que el factor de zoom cambia
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
        }


    }


    private class MyWebChromeClient extends WebChromeClient {

        // maneja la accion de seleccionar archivos

        //   Muestra el selector de archivos para '<input type = "file" />', devuelve false para usar el procesamiento predeterminado
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePath,
                                         FileChooserParams fileChooserParams) {


            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e("ErrorCreatingFile", "Unable to create Image File", ex);
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }

            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("*/*");

            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }


            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);


            return true;

        }


        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard

            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");

            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }

            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");

            mCapturedImageURI = Uri.fromFile(file);

            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");

            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[]{captureIntent});

            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);


        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {

            openFileChooser(uploadMsg, acceptType);
        }

        // Obtenga una lista de todos los elementos del historial de acceso para colorear el enlace.
        public void getVisitedHistory(ValueCallback<String[]> callback) {
        }

        // El control <video /> se mostrará como una imagen de póster cuando no se esté reproduciendo, que se puede especificar en HTML a través de su atributo 'poster'.
        public Bitmap getDefaultVideoPoster() {
            return null;
        }

        public View getVideoLoadingProgressView() {
            return null;
        }

        // Recibe el progreso de carga de la página actual
        public void onProgressChanged(WebView view, int i) {

            progress.setProgress(i);
            if (i == 100) {
                progress.setVisibility(View.GONE);

            } else {
                progress.setVisibility(View.VISIBLE);

            }
        }

        // Recibe el título del documento
        @TargetApi(Build.VERSION_CODES.N)
        public void onReceivedTitle(WebView view, String title) {
            //super.onReceivedTitle(view, title);
            //Toast.makeText(getApplicationContext(), "onLoadResource "+title, Toast.LENGTH_LONG).show();

            // Obtenga un juicio por título debajo de Android 6.0
            //   if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (detectError != 1) {
                //Toast.makeText(getApplicationContext(), "title" + title, Toast.LENGTH_LONG).show();
                if (title.equals("404") || title.equals("500") ||
                        title.equals("Error") || title.equals("Página web no disponible") ||
                        title.equals("Página de error 404 GPE") ||
                        title.equals(" No se puede abrir la página web ") || title.equals("Webpage not available")) {
                    //Toast.makeText(getApplicationContext(), "ERROR " + title, Toast.LENGTH_LONG).show();
                    // view.stopLoading();
                    //view.clearView();
                    String ErrorPagePath = "file:///android_asset/error404.html";
                    view.loadUrl(ErrorPagePath);
                    VisibleFloating();
                    detectError = 0;


                }
            }

            // }

        }

        // Icono de recepción (favicon)
        public void onReceivedIcon(WebView view, Bitmap icon) {
        }

        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        }

        // Notifique a la aplicación que la página actual ha entrado en modo de pantalla completa, en este momento la aplicación debe mostrar una Vista personalizada que contenga el contenido de la página web
        public void onShowCustomView(View view, CustomViewCallback callback) {
        }

        // Notificar a la aplicación que la página actual ha salido del modo de pantalla completa, en este momento la aplicación debe ocultar la vista personalizada mostrada anteriormente
        public void onHideCustomView() {
        }

        // Mostrar un cuadro de diálogo de alerta
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

            showAlert(message);
            // Nota:
            // Este código es obligatorio: result.confirm () significa:
            // Se determina el resultado del procesamiento y se despierta el hilo WebCore
            // de lo contrario no puede continuar haciendo clic en el botón

            result.confirm();

            return true;
        }

        // Mostrar un diálogo de confirmación
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            //showAlert(message);

            // Nota:
            // Este código es obligatorio: result.confirm () significa:
            // Se determina el resultado del procesamiento y se despierta el hilo WebCore
            // de lo contrario no puede continuar haciendo clic en el botón
            result.confirm();
            return true;
        }

        // Mostrar un cuadro de diálogo de solicitud
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            // Determine si es la URL requerida de acuerdo con los parámetros del protocolo (el principio es el mismo que el método 2)
            // Generalmente juzgado según el esquema (formato del protocolo) y la autoridad (nombre del protocolo) (los dos primeros parámetros)
            // Suponiendo que la URL entrante = "js: // webview? Arg1 = 111 & arg2 = 222" (también acordó ser interceptada)

          /*  Uri uri = Uri.parse(message);
            // Si el protocolo de la url = el protocolo js previamente acordado
            // Simplemente analiza los parámetros
            if ( uri.getScheme().equals("js")) {

                // Si autoridad = vista web en el acuerdo previamente acordado, los representantes cumplen con el acuerdo acordado
                // Entonces, intercepta la URL, el siguiente JS comienza a llamar al método que necesita Android
                if (uri.getAuthority().equals("demo")) {

                    //
                    // Ejecuta la lógica que JS necesita llamar
                    System.out.println ("js llamado el método de Android");
                    // puede tener parámetros en el protocolo y pasarse a Android
                    HashMap<String, String> params = new HashMap<>();
                    Set<String> collection = uri.getQueryParameterNames();

                    // Resultado del parámetro: representa el valor de retorno del cuadro de mensaje (valor de entrada)
                    result.confirm ("js llamó con éxito el método de Android");
                }
                return true;
            }*/
            return super.onJsPrompt(view, url, message, defaultValue, result);
            //return true;
        }

        // Mostrar un cuadro de diálogo para permitir al usuario elegir si desea abandonar la página actual
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return true;
        }

        // El contenido web de la fuente especificada intenta usar la API de geolocalización sin establecer permisos.
        // A partir de API24, este método solo se llama para fuentes seguras (https), las fuentes no seguras serán rechazadas automáticamente
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        }

        // Cuando se cancela la llamada actual a onGeolocationPermissionsShowPrompt (), la IU relacionada se oculta.
        public void onGeolocationPermissionsHidePrompt() {
        }

        // Notificar a la aplicación para abrir una nueva ventana
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            return false;
        }

        // Notificar a la aplicación para cerrar la ventana
        public void onCloseWindow(WebView window) {
        }

        // solicitud de enfoque
        public void onRequestFocus(WebView view) {
        }

        // Notificar al contenido de la página web de la aplicación para solicitar permiso para acceder al recurso especificado (el permiso no está autorizado ni denegado)
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void onPermissionRequest(PermissionRequest request) {
            request.grant(request.getResources());
//            Log.e("appNomeLogs", "|> onPermissionRequest");
//
//            MainActivity.this.runOnUiThread(new Runnable(){
//                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//                @Override
//                public void run() {
//                    Log.e("appNomeLogs", "|> onPermissionRequest run");
//                    request.grant(request.getResources());
//                }// run
//            });//
        }

        // Notifique que la solicitud de permiso de la aplicación ha sido cancelada y oculte la IU relevante.
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void onPermissionRequestCanceled(PermissionRequest request) {
        }

        // Muestra el selector de archivos para '<input type = "file" />', devuelve false para usar el procesamiento predeterminado
//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//            return false;
//        }
        // Recibir mensaje de consola de JavaScript
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return false;
        }


    }

    private void showAlert(String msj) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
        localBuilder.setMessage(msj).setPositiveButton("Ok", null);
        localBuilder.setCancelable(false);
        localBuilder.create().show();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showAlertSalirApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("¿Seguro que deseas salir?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        dialogInterface.dismiss();
                        NoVisibleFloating();
                        Toast.makeText(getApplicationContext(), "Cerrando la Aplicacion",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();


    }

    private void VisibleFloating() {
        floatingActionButton.setVisibility(View.VISIBLE);
    }

    private void NoVisibleFloating() {
        floatingActionButton.setVisibility(View.GONE);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
//            Toast.makeText(getApplicationContext(), webView.getUrl(),
//                    Toast.LENGTH_LONG).show();
        //  Log.i("ansen", "¿Hay una página anterior:" + webView.canGoBack());
        NoVisibleFloating();
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {// ¿Hay una página anterior cuando hago clic en el botón Atrás


            if (webView.getUrl().equals("https://gpetest.simbiosis-dg-apps.com/menu.html")) {
                showAlertSalirApp();
            } else if (webView.getUrl().equals("file:///android_asset/error404.html") && detectError == 1) {
                detectError = 0;
                showAlertSalirApp();
                // finish();
                //Toast.makeText(getApplicationContext(), "Cerrando la Aplicacion",
                //       Toast.LENGTH_LONG).show();
                //webView.goBack();
            } else if (webView.getUrl().equals("file:///android_asset/error404.html") && detectError == 0) {
                webView.loadUrl("https://gpetest.simbiosis-dg-apps.com/menu.html");
            } else {
                webView.goBack(); // goBack () significa volver a la página anterior de webView
            }
            return true;
        } else {
            showAlertSalirApp();
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        // Active WebView para que esté activo, y la respuesta de la página web se puede ejecutar normalmente
        try {
            getApplicationContext().registerReceiver(updateRecirver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        } catch (Exception e) {
            // already registered
        }


    }

    @Override
    protected void onPause() {

        // Cuando la página pierde el foco y cambia al estado invisible de fondo, debe ejecutar onPause
        // Mediante la acción onPause para notificar al kernel que pause todas las acciones, como el análisis del DOM, la ejecución de complementos y la ejecución de JavaScript.
        webView.onPause();
        try {
            if (updateRecirver != null) {
                unregisterReceiver(updateRecirver);
                updateRecirver = null;
            }
        } catch (Exception e) {
            // already unregistered
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        try {
            getApplicationContext().unregisterReceiver(updateRecirver);
        } catch (Exception e) {
            // already unregistered
        }
        super.onStop();
    }

    protected void onDestroy() {

        if(updateRecirver !=null){
            getApplicationContext().unregisterReceiver(updateRecirver);
           //this.updateRecirver = null;
        }

        if (webView != null) {
            webView.setWebViewClient(null);
            webView.setWebChromeClient(null);
            // Cargar contenido nulo
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            // Borrar historial
            webView.clearHistory();
            // Eliminar webView
            ((ViewGroup) webView.getParent()).removeView(webView);
            //Destruir
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }


    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }


    // Solicita los permisos,
    //     * web intents (geo, mailto, tel, sms, market, etc)
    //     * acceso al sistema de archivos en ambos sentidos
    //     * acceso a la cámara
    //     * acceso a Internet
    //     * permiso de acceso a geolocalización
    //     * permiso para descargar archivos generados localmente mediante javascript
    // 1, primero declara una matriz de permisos y coloca los permisos necesarios
    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA


    };
    // 2. Cree una mPermissionList y determine qué permisos no se otorgan uno por uno, y los permisos no autorizados se almacenan en mPerrrmissionList
    List<String> mPermissionList = new ArrayList<>();

    // Sentencia y solicitud de permiso
    private void initPermission() {

        mPermissionList.clear();// Borrar el permiso que no ha pasado
        // Uno por uno, juzgue si se ha aprobado el permiso que desea
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);// Agregar permisos que no se han otorgado
            }
        }
        // solicitud de acceso
        if (mPermissionList.size() > 0) {// No se pudo aprobar el permiso, es necesario solicitarlo
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        } else {
           /* MDToast.makeText(MainActivity.this,
                    "Permisos otorgados a la aplicación.",
                    Toast.LENGTH_LONG, MDToast.TYPE_SUCCESS).show();*/

            // Se ha aprobado el permiso, puedes hacer lo que quieras hacer
        }
    }

    private final int mRequestCode = 100;// Código de solicitud de permiso


    // Método de devolución de llamada después de solicitar permiso
    // Parámetro: requestCode es nuestro propio código de solicitud de permiso
    // Parámetros: permisos es una matriz de nombres de permisos que solicitamos
    // Parámetros: grantResults es una matriz de identificación de si permitimos el permiso después de que aparezca la página, la longitud de la matriz corresponde a la longitud de la matriz de nombres de permisos, los datos de la matriz 0 significan permiso y -1 significa que hicimos clic en el permiso de prohibición
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;// Permiso fallido
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            // Si no se permite el permiso
            if (hasPermissionDismiss) {
                showPermissionDialog();// Vaya a la página de permisos de configuración del sistema o cierre directamente la página para evitar que continúe visitando
            } else {
                // Se pasan todos los permisos, puede continuar con el siguiente paso. . .

            }
        }

    }

    AlertDialog mPermissionDialog;
    String mPackName = "com.huawei.liwenzhi.weixinasr";

    private void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("Permiso deshabilitado, otorgue manualmente")
                    .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Cierra la página o realiza otras operaciones
                            cancelPermissionDialog();

                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    // Cerrar el diálogo
    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            if (requestCode == FILECHOOSER_RESULTCODE) {

                if (null == this.mUploadMessage) {
                    return;

                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {

                        result = null;

                    } else {

                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;

            }
        }

        return;
    }
}
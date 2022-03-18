//package cu.simbiosissurl.gpetest;
//
//import android.annotation.SuppressLint;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.util.Log;
//import android.view.View;
//import android.widget.Toast;
//
//import com.google.android.material.snackbar.Snackbar;
//
//
//public class UpdateRecirver extends BroadcastReceiver  {
//
//    Context context;
//
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        @SuppressLint("MissingPermission") NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
//        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
//        if (isConnected) {
//            // Toast.makeText(context, "Conectado "+ isConnected,
//            //        Toast.LENGTH_LONG).show();
//            Log.i("NET", "Not Connected" + isConnected);
//
//
//        } else {
//            // Create the Snackbar
//            View parentLayout = app.findViewById(android.R.id.content);
//            Snackbar snackbar = Snackbar.make(parentLayout, "¡Parece que no estás conectado a Internet!. " +
//                    "Pueden existir páginas " +   "no guardadas en cache que necesiten " +
//                    "de conexion a intennet ", Snackbar.LENGTH_LONG);
//// Get the Snackbar's layout view
//            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
////If the view is not covering the whole snackbar layout, add this line
//            layout.setPadding(0, 0, 0, 0);
//// Show the Snackbar
//            snackbar.show();
////            Toast.makeText(context, "¡Parece que no estás conectado a Internet!. Pueden existir páginas " +
////                            "no guardadas en cache que necesiten de conexion a intennet ",
////                    Toast.LENGTH_LONG).show();
////            Log.i("NET", "Not Connected" + isConnected);
//
//        }
//
//
//    }
//}
//

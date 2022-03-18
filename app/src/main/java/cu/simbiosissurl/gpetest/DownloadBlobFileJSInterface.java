package cu.simbiosissurl.gpetest;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


class DownloadBlobFileJSInterface extends AppCompatActivity {

    String name = "";
    String archivio = "";
    String extencion = "";
    private String FileName = "";
    private Context mContext;
    int counter = 0;
    private DownloadGifSuccessListener mDownloadGifSuccessListener;

    public DownloadBlobFileJSInterface(Context context) {
        this.mContext = context;
    }

    public void setDownloadGifSuccessListener(DownloadGifSuccessListener listener) {
        mDownloadGifSuccessListener = listener;
    }


    @JavascriptInterface
    public void callJS() {
        System.out.println("aaaaa");

        // DownloadAsyncTask task = new DownloadAsyncTask();
        // task.execute(base64Data ,getFileName());
        //convertToGifAndProcess(base64Data);
    }
    @JavascriptInterface
    public void validate() {
        //do some action...
    }

    @JavascriptInterface
    public void cancel() {
        //do some actions...
    }

    @JavascriptInterface
    public void getBase64FromBlobData(String base64Data) {
        // DownloadAsyncTask task = new DownloadAsyncTask();
        // task.execute(base64Data ,getFileName());

        convertToGifAndProcess(base64Data);
        //return base64Data;

    }

    public static String getBase64StringFromBlobUrl(String blobUrl) {
        if (blobUrl.startsWith("blob")) {
            return "javascript: var xhr = new XMLHttpRequest();" +
                    "xhr.open('GET', '" + blobUrl + "', true);" +
                    "xhr.setRequestHeader('Content-type','text/plain','name');" +
                    "xhr.getAllResponseHeaders();" +
                    "xhr.responseType = 'blob';" +
                    "xhr.onload = function(e) {" +
                    "    if (this.status == 200) {" +
                    "        var blobFile = this.response;" +
                    "        var reader = new FileReader();" +
                    "        reader.readAsBinaryString(blobFile);" +
                    "        reader.onloadend = function() {" +
                    "            base64data = reader.result;" +
                    "        Android.getBase64FromBlobData(base64data);" +
                    "        }" +
                    "    }" +
                    "};" +

                    "xhr.send();";

        }
        return "javascript: console.log('It is not a Blob URL');";
    }


    private void convertToGifAndProcess(String base64) {
        Date c = Calendar.getInstance().getTime();
        //SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy-HH:mm:ss", Locale.getDefault());
        //String formattedDate = df.format(c);

        String contenArchivo = picarString(base64);
        //Log.e("contenArchivo ", contenArchivo);
        //String filed = String.valueOf(mContext.getFilesDir());
        //String filed = Environment.getExternalStoragePublicDirectory("").getAbsolutePath() + "/gpe/";
        // String filed = Environment.getExternalStorageDirectory().getAbsolutePath() + "/gpe/";
        //File directory = new File(filed); 
        File directory;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            directory = mContext.getExternalFilesDir("/export");
            // File gifFile = new File(directory, getFileName() + extencion);
            // directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/gpe");
        } else {
            directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/gpe");
        }
        if (!directory.exists())
            directory.mkdirs();
        File gifFile = new File(directory, getFileName() + extencion);
        //     File gifFile = new File(directory, getFileName() + "("+ counter +")" + extencion);
//        File documentDirectory = File(ContextCompat.getExternalFilesDirs(
//                mContext, Environment.DIRECTORY_DOWNLOADS), "APPdeYosvany");
        // val file = File(documentDirectory , "Nombre del fichero")
//       File gifFile = new File(Environment.getExternalStoragePublicDirectory(
//                  Environment.DIRECTORY_DOWNLOADS) + "/gpe/" + getFileName() + "("+ formattedDate +")" + extencion);

//
//        if (gifFile.exists()) {
//            counter++;
//            //   gifFile ++;
//        }
        try {


            byte[] data = contenArchivo.getBytes("UTF-8");
            String base = Base64.encodeToString(data, Base64.DEFAULT);
            saveGifToPath(base, gifFile);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //String ics = getFileName().substring(getFileName().lastIndexOf("."));
        if (extencion.equals(".ics")) {
            try {
                //File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/gpe");
                // File file = new File(directory, getFileName());
                String type = getMIMEType(gifFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri apkURI = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", gifFile);
                    intent.setDataAndType(apkURI, type);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    intent.setDataAndType(Uri.fromFile(gifFile), type);
                }
                mContext.startActivity(intent);

            } catch (Exception e) {
                Toast toast = Toast.makeText(mContext, "Error" + e, Toast.LENGTH_LONG);
                toast.show();
                Log.e("error", "e:" + e);
            }

        } else {
            Log.e("error", "e:");
            //showAlert();
        }

        //  }


        if (mDownloadGifSuccessListener != null) {
            mDownloadGifSuccessListener.downloadGifSuccess(gifFile.getAbsolutePath());
        }
    }

    // @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("NewApi")
    public String picarString(String base64) {
        //String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
        // Date currentDateTime = Calendar.getInstance().getTime();

        String[] separated = base64.split(";", -1);
        String[] a = Arrays.copyOfRange(separated, 0, 1); //<- (targetArray, start, to)
        name = String.join("", a);
        String[] b = Arrays.copyOfRange(separated, 1, separated.length);
        archivio = String.join(";", b);

        byte ptext[] = new byte[0];
        byte ptextarchivo[] = new byte[0];
        try {
            ptext = name.getBytes("ISO-8859-1");
            ptextarchivo = archivio.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            archivio = new String(ptextarchivo, "UTF-8");
            String value = new String(ptext, "UTF-8");
            String[] separatename = value.split("\\.");
            String nombre = separatename[0];
            extencion = "." + separatename[1];
            setFilname(nombre);
            //setFilname(value);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return archivio;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            //byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
           // byte[] imageBitmap = Base64.decode(encodedString,Base64.DEFAULT);
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            Bitmap bitmap = BitmapFactory.decodeFile(encodedString);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] imageBytes = baos.toByteArray();
//            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void saveGifToPath(String base64, File gifFilePath) {
        try {

            FileOutputStream os = new FileOutputStream(gifFilePath, false);
//            if (extencion.equals(".png") || extencion.equals(".jpeg")) {
//               // Bitmap imag = StringToBitMap(base64);
//               // byte[] imageAsBytes = Base64.decode(base64.getBytes(), Base64.DEFAULT);
//               // imageAsBytes.compress(Bitmap.CompressFormat.PNG, 90, os);
//                //os.write(imageAsBytes);
//               // ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                //imag.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//               // byte[] byteArray = byteArrayOutputStream .toByteArray();
//               // byte[] img = EntityUtils.toByteArray(base64);
//                byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
//                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                Log.e("decodedByte", "" + decodedByte) ;
//                //Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
//
//            } else {
//                // Log.e("tag", ":" + fileBytes);
//                byte[] fileBytes = Base64.decode(base64, 0);
//                os.write(fileBytes);
//
//
//            }
            byte[] fileBytes = Base64.decode(base64, 0);
            os.write(fileBytes);
            os.flush();
            os.close();
            //return true;

        } catch (Exception e) {
            e.printStackTrace();
            // return false;
        }

    }

    public interface DownloadGifSuccessListener {
        void downloadGifSuccess(String absolutePath);
    }

    public Intent getFileIntent(File file) {
        // Uri uri = null;
//		 Uri uri = Uri.parse("http://m.ql18.com.cn/hpf10/1.pdf");
        //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //    uri = Uri.parse(String.valueOf(file));
        // } else {
        Uri uri = Uri.fromFile(file);
        // imageUri = Uri.fromFile(new File(filepath));
        // }

        String type = getMIMEType(file);
        //Log.i("tag", "type=" + type);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setDataAndType(uri, type);
        return intent;


    }

    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        // Obtener extensión
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();

        //Determinar MimeType según el tipo de extensión *
        if (end.equals("pdf")) {
            type = "application/pdf";//
        } else if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
                end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio/*";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video/*";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
                end.equals("jpeg") || end.equals("bmp")) {
            type = "image/*";
        } else if (end.equals("apk")) {
            /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        } else if (end.equals("ics")) {
            /* android.permission.INSTALL_PACKAGES */
            type = "text/*";
        }
//      else if(end.equals("pptx")||end.equals("ppt")){
//    	  type = "application/vnd.ms-powerpoint";
//      }else if(end.equals("docx")||end.equals("doc")){
//    	  type = "application/vnd.ms-word";
//      }else if(end.equals("xlsx")||end.equals("xls")){
//    	  type = "application/vnd.ms-excel";
//      }
        else {
// / * Si no se puede abrir directamente, salte de la lista de software para que el usuario elija * /
            type = "*/*";
        }
        return type;
    }

    public void setFilname(String filname) {
        FileName = filname;
    }

    public String getFileName() {
        return FileName;
    }


}


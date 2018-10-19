package com.melonltd.naber.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.melonltd.naber.model.constant.NaberConstant;

import java.io.ByteArrayOutputStream;

public class PhotoTools {
//    private static final String TAG = PhotoTools.class.getSimpleName();


    private static final  int MULTIPLE = 4;
    public static Bitmap sampleBitmap(Bitmap bitmap , int minLenDP) {
        int dp = minLenDP * MULTIPLE;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        byte[] bytes = out.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0 , bytes.length, options);
        int height = options.outHeight;
        int width= options.outWidth;
        int inSampleSize = 1; // 默认像素压缩比例，压缩为原图的1/2
        int minLen = Math.max(height, width); // 原图的最小边长
        if(minLen > dp) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
            float ratio = (float)minLen /dp; // 计算像素压缩比例
            inSampleSize = (int)ratio;
        }
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
        Bitmap bbm = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length, options); // 解码文件
        return bbm;
    }


//    public static byte[] sampleToByteArray(Bitmap bitmap , int minLenDP) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        byte[] bytes = out.toByteArray();
////        BitmapFactory.decodeByteArray(bytes, 0 , bytes.length, options);
//        int height = options.outHeight;
//        int width= options.outWidth;
//        int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
//        int minLen = Math.min(height, width); // 原图的最小边长
//        if(minLen > minLenDP) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
//            float ratio = (float)minLen / 100.0f; // 计算像素压缩比例
//            inSampleSize = (int)ratio;
//        }
//        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
//        options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
//        Bitmap bbm = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length, options); // 解码文件
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bbm.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        return outputStream.toByteArray();
//    }

    public static StorageReference getReference(String path, String child, String fileName){
        return FirebaseStorage
                .getInstance(path)
                .getReference()
                .child(child + fileName);
    }


//    public static Bitmap drawableToBitmap(Drawable drawable) {
//        Bitmap bitmap = Bitmap
//                .createBitmap(
//                        drawable.getIntrinsicWidth(),
//                        drawable.getIntrinsicHeight(),
//                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                                : Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(bitmap);
//        //canvas.setBitmap(bitmap);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        drawable.draw(canvas);
//        return bitmap;
//    }

//    public static Bitmap bytes2Bimap(byte[] b) {
//        if (b.length != 0) {
//            return BitmapFactory.decodeByteArray(b, 0, b.length);
//        } else {
//            return null;
//        }
//    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

//    public static String bitmap2Base64String(Bitmap bm) {
//        byte[] b64file = Base64.encode(bitmap2Bytes(bm), Base64.DEFAULT);
//        return new String(b64file, StandardCharsets.UTF_8);
//    }

//    public static byte[] bitmap2Base64(Bitmap bm) {
//        byte[] b64file = Base64.encode(bitmap2Bytes(bm), Base64.DEFAULT);
//        return b64file;
//    }

//    public static File bitmap2File(Bitmap bm, File file, String filename) throws IOException {
//        File f = new File(file, filename);
//        f.createNewFile();
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100/*ignored for PNG*/, bos);
//        byte[] bitmapdata = bos.toByteArray();
//        FileOutputStream fos = new FileOutputStream(f);
//        fos.write(bitmapdata);
//        fos.flush();
//        fos.close();
//        return f;
//    }

    public static void upLoadImage(byte[] bytes, String sourcePath, String fileName, final UpLoadCallBack callback){
        final StorageReference ref = PhotoTools.getReference(NaberConstant.STORAGE_PATH, sourcePath, fileName);
        ref.putBytes(bytes).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.failure(exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot task) {

            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    callback.getUri(downloadUri);
                }
            }
        });
    }

}

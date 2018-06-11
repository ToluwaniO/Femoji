package toluog.femoji;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;

import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import static android.content.Context.CAMERA_SERVICE;

public class FaceUtil {

    private String TAG = FaceUtil.class.getSimpleName();
    private static final float EMOJI_SCALE_FACTOR = 1.0f;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, Context context)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        CameraManager cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360;

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        int result;
        switch (rotationCompensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                Log.e(TAG, "Bad rotation value: " + rotationCompensation);
        }
        return result;
    }

    public static Bitmap drawSquares(Bitmap start, Rect bounds) {
        Bitmap res = Bitmap.createScaledBitmap(start, start.getWidth(), start.getHeight(), true);
        Canvas canvas = new Canvas(res);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(20f);
        //canvas.drawRect(bounds, paint);
        //canvas.drawCircle(bounds.width(), bounds.height(), bounds.height()/2, paint);
        canvas.drawOval(bounds.left, bounds.top, bounds.right, bounds.bottom, paint);
        return res;
    }

    public static Bitmap drawFaces(Bitmap first, Bitmap second, Rect bounds) {
        Bitmap res = Bitmap.createBitmap(first.getWidth(), first.getHeight(), first.getConfig());


        int emojiWidth = (int) (bounds.width() * EMOJI_SCALE_FACTOR);
        int emojiHeight = (int) (second.getHeight() * emojiWidth / second.getWidth() * EMOJI_SCALE_FACTOR);

        second = Bitmap.createScaledBitmap(second, emojiWidth, emojiHeight, false);
        float emojiPositionX =
                (bounds.left + bounds.width() / 2) - second.getWidth() / 2;
        float emojiPositionY =
                (bounds.top + bounds.height() / 2) - second.getHeight() / 3;

        Canvas canvas = new Canvas(res);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second, emojiPositionX, emojiPositionY, null);
        return res;
    }

}

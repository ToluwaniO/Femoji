package toluog.femoji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.otaliastudios.cameraview.CameraUtils;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditImageFragment extends Fragment {

    private String TAG = EditImageFragment.class.getSimpleName();
    byte[] rawImage;
    private Bitmap startImage, finalImage;
    private ImageView finalImageView;
    private FloatingActionButton closeEmojis, insertEmojis, done;
    private ConstraintLayout selectLayout;
    private RecyclerView emojiRecycler;
    private EmojiAdapter adapter;
    private static String ARG_ARRAY = "Picture array";
    private List<FirebaseVisionFace> faces;
    private int[] emojis = {R.drawable.frown_face, R.drawable.one_eye_closed_emoji, R.drawable.smile_emoji,
    R.drawable.shit_emoji, R.drawable.robot_emoji, R.drawable.alien_emoji, R.drawable.innocent_emoji,
    R.drawable.heart_eyes_emoji, R.drawable.nerd_emoji, R.drawable.devil_emoji, R.drawable.shush_emoji,
    R.drawable.cowboy_emoji, R.drawable.sneeze_emoji, R.drawable.exploding_emoji, R.drawable.crying_emoji};

    private OnSuccessListener<List<FirebaseVisionFace>> sListener = new OnSuccessListener<List<FirebaseVisionFace>>() {
        @Override
        public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
            Log.d(TAG, firebaseVisionFaces.size() + " faces found");
            faces = firebaseVisionFaces;
            for (FirebaseVisionFace face : firebaseVisionFaces) {
                handleFaces(face);
            }
        }
    };

    private OnFailureListener fListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.d(TAG, e.getMessage());
        }
    };

    private OnFragmentInteractionListener mListener;

    public EditImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param startImage Parameter 1.
     * @return A new instance of fragment EditImageFragment.
     */
    public static EditImageFragment newInstance(byte[] startImage) {
        EditImageFragment fragment = new EditImageFragment();
        Bundle args = new Bundle();
        args.putByteArray(ARG_ARRAY, startImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rawImage = getArguments().getByteArray(ARG_ARRAY);
            //startImage = BitmapFactory.decodeByteArray(data, 0, data.length);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_image, container, false);
        finalImageView = v.findViewById(R.id.finalImageView);
        emojiRecycler = v.findViewById(R.id.emoji_recycler);
        closeEmojis = v.findViewById(R.id.close_emojis);
        insertEmojis = v.findViewById(R.id.insert_emojis);
        selectLayout = v.findViewById(R.id.select_layout);
        done = v.findViewById(R.id.done);

        adapter = new EmojiAdapter(this, emojis);
        emojiRecycler.setLayoutManager(new LinearLayoutManager(v.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        emojiRecycler.setAdapter(adapter);

        insertEmojis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertEmojis.setVisibility(View.GONE);
                selectLayout.setVisibility(View.VISIBLE);
            }
        });

        closeEmojis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertEmojis.setVisibility(View.VISIBLE);
                selectLayout.setVisibility(View.GONE);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.pictureEmojified(finalImage);
            }
        });

        CameraUtils.decodeBitmap(rawImage, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                startImage = bitmap;
                finalImageView.setImageBitmap(startImage);
                initFVision();
            }
        });

        return v;
    }

    private void initFVision() {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
                        .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .setMinFaceSize(0.15f)
                        .setTrackingEnabled(true)
                        .build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(startImage);
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options);
        Task<List<FirebaseVisionFace>> result = detector.detectInImage(image)
                .addOnSuccessListener(sListener)
                .addOnFailureListener(fListener);
    }

    private void handleFaces(FirebaseVisionFace face) {
        finalImage = FaceUtil.drawSquares(startImage, face.getBoundingBox());
        finalImageView.setImageBitmap(finalImage);
    }

    public void embedEmoji(int index) {
        Bitmap second = BitmapFactory.decodeResource(getContext().getResources(), emojis[index]);

        if(faces != null && faces.size() > 0) {
            for (FirebaseVisionFace face : faces) {
                finalImage = FaceUtil.drawFaces(startImage, second, face.getBoundingBox());
            }
        }
        finalImageView.setImageBitmap(finalImage);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void pictureEmojified(Bitmap bitmap);
    }
}

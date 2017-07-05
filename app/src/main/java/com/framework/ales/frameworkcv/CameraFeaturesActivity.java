package com.framework.ales.frameworkcv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import static java.util.Arrays.*;

public class CameraFeaturesActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener {

    public static final int VIEW_MODE_RGBA = 0;
    public static final int VIEW_MODE_HCIRCLES = 1;
    public static final int VIEW_MODE_HLINES = 2;
    public static final int VIEW_MODE_CANNY = 3;
    public static final int VIEW_MODE_COLCONTOUR = 4;
    public static final int VIEW_MODE_FACEDETECT = 5;
    //public static final int VIEW_MODE_YELLOW_QUAD_DETECT = 6;
    public static final int VIEW_MODE_GFTT = 7;
   // public static final int VIEW_MODE_OPFLOW = 8;
    public static final int VIEW_MODE_PIXEL = 9;
    public static final int VIEW_MODE_GRAY = 10;
    public static final int VIEW_MODE_COLORCANNY = 11;

    private long fps = 0, pocatekMilis = 0, nynejsiMilis = 0, casSnimkuMilis = 0;

    public static int viewMode = VIEW_MODE_RGBA;

    private CascadeClassifier mCascadeClassifier;

    private boolean bSnimek = false, bDisplayNazev = true, bPrvniOblicejUlozen = false;

    private byte[] bSledovaniBarvy;

    private double d, dVelikostTextu, x1, x2, y1, y2;

    private double[] vectorHough;

    private File cascadeFile;
    private Point pt, pt1, pt2;

    private int x, y, radius, minRadius, maxRadius, cannySpodniPrah, cannyHorniPrah,
    akumulator, sirkaCar = 3, houghuvPrah = 50, minVelikostHoughovyUsecky = 20,
    useckovaMezera = 20, maxVyskaObliceje, maxVyskaOblicejeIndex, poradiSouboru = 0,
    kamera = 0, pocetKamer = 0, gFFTMax = 40, minOblastObrysu = 1000;

    private JavaCameraView javaCameraView1;
    private JavaCameraView javaCameraView2;

    private Camera androidCamera1;
    private Camera androidCamera2;

    private List<Byte> byteStatus;
    private List<Integer> intHueMap, intChannels;
    private List<Float> fRanges;
    private List<Point> pPts, pCorners, pCornersThis, pCornersPrev;
    private List<MatOfPoint> contours;

    private Mat mRgba, mGray, mIntermediateMat, mMatRed, mMatGreen, mMatBlue, mROIMat,
            mMatRedInv, mMatGreenInv, mMatBlueInv, mHSVMat, mErodeKernel, mContours,
            lines, mFaceDest, mFaceResized, matOpFlowPrev, matOpFlowThis,
            matFaceHistogramPrevious, matFaceHistogramThis, mHist;

    private MatOfFloat mofErr, mofRange;
    private MatOfRect morFaces;
    private MatOfByte mobStatus;
    private MatOfPoint2f mop2F1, mop2F2, mop2PtsPrev, mop2PtsThis, mop2PtsSave, mofApproxContour;
    private MatOfPoint mopCorners;
    private MatOfInt moiOne, moiSize;
    private Vector<Vector> hierarchy;

    private Rect rect, rDestin;

    private Scalar colorRed, colorGreen, colorYellow;

    private Size size, size3, size5, sizeMat;

    private String text, textShot;

    private  MenuItem spinnerItem;

    private Thread thread;

    private void feedMultiple(){
        if (thread!=null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onCameraFrame(mRgba);
            }
        };

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++){
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(5);

                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    javaCameraView1.enableView();

                    if (pocetKamer > 1)
                        javaCameraView2.enableView();

                    try {

                        InputStream inputStream = getResources().openRawResource(R.raw.haarcascade_frontalface_default);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        cascadeFile = new File(cascadeDir, "haarcascade_frontalface_default.xml");

                        FileOutputStream fileOutputStream = new FileOutputStream(cascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1){
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }
                        inputStream.close();
                        fileOutputStream.close();


                        mCascadeClassifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
                        mCascadeClassifier.load(cascadeFile.getAbsolutePath());
                        System.out.println("Nacteno");
                        if (mCascadeClassifier.empty()){
                            mCascadeClassifier = null;
                            Log.d(TAG, "Cascada je prazdna");
                            System.out.println("Praydno");


                        }


                        cascadeDir.delete();

                    }
                    catch (IOException e){
                        e.printStackTrace();
                        Log.d(TAG, "Failed to load cascade. Exception thrown: " + e);
                        System.out.println("Chyba");
                    }
                }
                break;
                default:
                {
                    super.onManagerConnected(status);
                }
                break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera_features);

        final ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowTitleEnabled(false);
       // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        final String[] dropdown = getResources().getStringArray(R.array.erode);

       // ArrayAdapter<String> adapter = new ArrayAdapter<String>(actionBar.getThemedContext(),android.R.layout.simple_spinner_item, android.R.id.text1, dropdown);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //actionBar.setListNavigationCallbacks(adapter, this);
        pocetKamer = Camera.getNumberOfCameras();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        javaCameraView1 = (JavaCameraView) findViewById(R.id.java_camera_view1);
        if (pocetKamer > 1){
            javaCameraView2 = (JavaCameraView) findViewById(R.id.java_camera_view2);
        }
        javaCameraView1.setVisibility(SurfaceView.VISIBLE);
        javaCameraView1.setCvCameraViewListener(this);
        javaCameraView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        if (pocetKamer > 1){
            javaCameraView2.setVisibility(SurfaceView.GONE);
            javaCameraView2.setCvCameraViewListener(this);
            javaCameraView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, baseLoaderCallback);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){


        //vyber akce z menu
        if (item.getItemId() == R.id.action_rgbpreview){
            //barevna kamera
            viewMode = VIEW_MODE_RGBA;
            fps = 0;
            pocatekMilis = 0;
        } else if (item.getItemId() == R.id.action_gray){
            //cernobila kamera
            viewMode = VIEW_MODE_GRAY;
            fps = 0;
            pocatekMilis = 0;
        } else if (item.getItemId() == R.id.action_cannyedges){
            //zvyrazneni hran - cannyho algorytmus
            viewMode = VIEW_MODE_CANNY;
            fps = 0;
            pocatekMilis = 0;

        } else if (item.getItemId() == R.id.action_houghlines){
            //zvyrazneni linek - houghuv algorytmus
            viewMode = VIEW_MODE_HLINES;
            fps = 0;
            pocatekMilis = 0;

        } else if (item.getItemId() == R.id.action_houghcircles){
            //zvyrazneni kulatych predmetu - kruznic - houghuv algorytmus
            viewMode = VIEW_MODE_HCIRCLES;
            fps = 0;
            pocatekMilis = 0;
        }else if (item.getItemId() == R.id.action_colourcontour){
            // zvyrazneni vybrane barvy - definovane v promenne
            viewMode = VIEW_MODE_COLCONTOUR;
            fps = 0;
            pocatekMilis = 0;
        }else if (item.getItemId() == R.id.action_facedetect){
            // detekce obliceje
            viewMode = VIEW_MODE_FACEDETECT;
            fps = 0;
            pocatekMilis = 0;
            bPrvniOblicejUlozen = false;
        }else if (item.getItemId() == R.id.action_gftt){
            //algorytmus pro vyhledavani sledovatelnych objektu v obrazu
            viewMode = VIEW_MODE_GFTT;
            fps = 0;
            pocatekMilis = 0;
            bPrvniOblicejUlozen = false;
        }  else if (item.getItemId() == R.id.action_swapcamera){

            //zmena predni/zadni kamery - je-li dostupna
            if (pocetKamer > 1){
                if (kamera == 0){
                    javaCameraView1.setVisibility(SurfaceView.GONE);
                    javaCameraView2 = (JavaCameraView) findViewById(R.id.java_camera_view2);
                    javaCameraView2.setCvCameraViewListener(this);
                    javaCameraView2.setVisibility(SurfaceView.VISIBLE);
                    kamera = 1;
                } else {
                    javaCameraView2.setVisibility(SurfaceView.GONE);
                    javaCameraView1 = (JavaCameraView) findViewById(R.id.java_camera_view1);
                    javaCameraView1.setCvCameraViewListener(this);
                    javaCameraView1.setVisibility(SurfaceView.VISIBLE);
                    kamera = 0;
                }
            }
        }else if (item.getItemId() == R.id.action_toggletitles){
            //zapnuti/vypnuti popisku
            if (bDisplayNazev == true){
                bDisplayNazev = false;
            } else {
                bDisplayNazev = true;
            }
        }else if (item.getItemId() == R.id.action_pixelizace){
            //pixelizace
            viewMode = VIEW_MODE_PIXEL;
            fps = 0;
            pocatekMilis = 0;
        }



        return true;
    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        //inicializace promennych
        bSledovaniBarvy = new byte[3];
        bSledovaniBarvy[0] = 27;
        bSledovaniBarvy[1] = 100;
        bSledovaniBarvy[2] = (byte) 255;

        byteStatus = new ArrayList<Byte>();

        intChannels = new ArrayList<Integer>();
        intChannels.add(0);
        colorRed = new Scalar(255,0,0,255);
        colorGreen = new Scalar(0,255,0,255);
        colorYellow = new Scalar (255, 100, 50, 255);
        contours = new ArrayList<MatOfPoint>();
        pCorners = new ArrayList<Point>();
        pCornersPrev = new ArrayList<Point>();
        pCornersThis = new ArrayList<Point>();

        morFaces = new MatOfRect();

        moiSize = new MatOfInt(25);

        intHueMap = new ArrayList<Integer>();
        intHueMap.add(0);
        intHueMap.add(1);
        lines = new Mat();

        mofApproxContour = new MatOfPoint2f();

        mContours = new Mat();
        mHist = new Mat();
        mGray = new Mat();
        mHSVMat = new Mat();
        mIntermediateMat = new Mat();
        mMatRed = new Mat();
        mMatRedInv = new Mat();
        mMatBlue = new Mat();
        mMatBlueInv = new Mat();
        mMatGreen = new Mat();
        mMatGreenInv = new Mat();

        moiOne = new MatOfInt();

        mofRange = new MatOfFloat(0f, 256f);
        mop2F1 = new MatOfPoint2f();
        mop2F2 = new MatOfPoint2f();
        mop2PtsPrev = new MatOfPoint2f();
        mop2PtsThis = new MatOfPoint2f();
        mop2PtsSave = new MatOfPoint2f();
        mofErr = new MatOfFloat();
        mobStatus = new MatOfByte();
        mopCorners = new MatOfPoint();
        mRgba = new Mat();
        mROIMat = new Mat();
        mFaceDest = new Mat();
        mFaceResized = new Mat();
        matFaceHistogramPrevious = new Mat();
        matFaceHistogramThis = new Mat();
        matOpFlowThis = new Mat();
        matOpFlowPrev = new Mat();

        //body
        pt = new Point(0,0);
        pt1 = new Point(0,0);
        pt2 = new Point(0,0);
        pPts = new ArrayList<Point>();

        //rozsah
        fRanges = new ArrayList<Float>();
        fRanges.add(50.0f);
        fRanges.add(256.0f);
        rect = new Rect();
        rDestin = new Rect();

        sizeMat = new Size();
        size = new Size();
        size3 = new Size(3,3);
        size5 = new Size(5,5);

        text = "";

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int dpi = displayMetrics.densityDpi;
        dVelikostTextu = ((double)dpi/240.0)*0.7;

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height,width,CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        //pri vypnuti kamery vymazani poli
        releaseArrays();

    }

    private void releaseArrays() {
        //vymazani poli
        mRgba.release();
        mIntermediateMat.release();
        mGray.release();
        mMatRedInv.release();
        mMatRed.release();
        mMatBlue.release();
        mMatBlueInv.release();
        mMatGreen.release();
        mMatGreenInv.release();
        mROIMat.release();
        mHSVMat.release();
        mErodeKernel.release();
        mContours.release();
        mopCorners.release();
        mop2F1.release();
        mop2F2.release();
        mofApproxContour.release();
        lines.release();
        morFaces.release();
    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        
        minRadius = 20;
        maxRadius = 400;
        cannySpodniPrah = 50;
        cannyHorniPrah = 180;
        akumulator = 300;

        mErodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, size3);
        //inicializace casovani pro pocitani fps, obnova kazdych 10 sekund
        if (pocatekMilis == 0){
            pocatekMilis = System.currentTimeMillis();
            
        }
        if ((nynejsiMilis - pocatekMilis) > 10000){
            pocatekMilis = System.currentTimeMillis();
            fps = 0;
        }
        
        inputFrame.copyTo(mRgba);
        sizeMat.width = mRgba.width();
        sizeMat.height = mRgba.height();

        int rows = mRgba.width();
        int cols = mRgba.height();

        int left = cols / 8;
        int top = rows / 8;

        int width = cols * 3 / 4;
        int height = rows * 3 / 4;



        Mat rgbaWindow;
        
        //prepinani obrazovich funkci
        switch (viewMode){
            //barevny nahled kamery
            case VIEW_MODE_RGBA:
                if (bDisplayNazev)
                    ShowTitle("Barevná kamera", 1, colorYellow);

                break;


            case VIEW_MODE_GRAY:
                Imgproc.cvtColor(mRgba, mGray , Imgproc.COLOR_RGBA2GRAY);
                Imgproc.cvtColor(mGray, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);


                if (bDisplayNazev){
                    ShowTitle("Odstíny šedé", 1, colorYellow);
                }
                break;

            //efekt okraju
            case VIEW_MODE_CANNY:

               Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGBA2GRAY);
               // Imgproc.GaussianBlur(mGray, mGray, size3, 2, 2);
                cannyHorniPrah = 200;
                cannySpodniPrah = 35;

               // Imgproc.bilateralFilter(mRgba, mGray, -1, 50, 7);
                Imgproc.Canny(mGray, mIntermediateMat, cannySpodniPrah, cannyHorniPrah);


                Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);

                if (bDisplayNazev){
                    ShowTitle("Hrany", 1, colorYellow);
                }

                break;

            case VIEW_MODE_COLORCANNY:
                Mat canny = new Mat();
                Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGBA2GRAY);

                //Imgproc.bilateralFilter(mRgba, mGray, -1, 50, 7);

                Imgproc.Canny(mGray, mIntermediateMat, 35, 200);
                Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2BGRA, 4);


                if (bDisplayNazev){
                    ShowTitle("Barevné canny", 1, colorYellow);
                }

                break;

            //Houghova transformace pro kruznice
            case VIEW_MODE_HCIRCLES:

            Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGBA2GRAY);

            //pouziti gausianu pro zmenseni chyby
            Imgproc.GaussianBlur(mGray, mGray, size5, 2, 2);


                //nastaveni horni hranice
                cannyHorniPrah = 100;

                Imgproc.HoughCircles(mGray, mIntermediateMat, Imgproc.HOUGH_GRADIENT, 2.0, mGray.rows()/8, cannyHorniPrah, akumulator, minRadius, maxRadius);

                if (mIntermediateMat.cols() > 0){
                    for (int x = 0; x < Math.min(mIntermediateMat.cols(), 10); x++){
                        double vectCircle[] = mIntermediateMat.get(0,x);

                        if (vectCircle == null){
                            break;
                        }

                        pt.x = Math.round(vectCircle[0]);
                        pt.y = Math.round(vectCircle[1]);

                        radius = (int) Math.round(vectCircle[2]);

                        //vykresleni nalezeneho kruhu
                        Imgproc.circle(mRgba,pt,radius,colorGreen, sirkaCar);

                        //ve stredu kruhu namaluje kriz
                        NamalujStred (mRgba, colorGreen, pt);
                    }
                }

                if (bDisplayNazev){
                    ShowTitle("Houghovy kruznice", 1 , colorYellow);
                }

                break;

            //Houghova transformace pro usecky
            case VIEW_MODE_HLINES:

                Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGBA2GRAY);

                Imgproc.GaussianBlur(mGray, mGray, size5, 2, 2);

                //nastaveni meznich hodnot
                cannySpodniPrah = 45;
                cannyHorniPrah = 75;

                Imgproc.Canny(mGray, mGray, cannySpodniPrah, cannyHorniPrah);

                Imgproc.HoughLinesP(mGray, lines, 1, Math.PI/180, houghuvPrah, minVelikostHoughovyUsecky, useckovaMezera);

                for (int x = 0; x < Math.min(lines.cols(), 40); x++){
                    vectorHough = lines.get(0, x);

                    if (vectorHough.length == 0)
                        break;

                    x1 = vectorHough[0];
                    y1 = vectorHough[1];
                    x2 = vectorHough[2];
                    y2 = vectorHough[3];

                    pt1.x = x1;
                    pt1.y = y1;
                    pt2.x = x2;
                    pt2.y = y2;

                    Imgproc.line(mRgba, pt1, pt2, colorGreen, 3);
                }

                if (bDisplayNazev){
                    ShowTitle("Houghovy usecky", 1, colorYellow);
                }
                break;

            //obtazeni obrysu
            case VIEW_MODE_COLCONTOUR:

                //konverze RGBA obrazu do HSV
                Imgproc.cvtColor(mRgba, mHSVMat, Imgproc.COLOR_RGB2HSV, 5);

                Core.inRange(mHSVMat, new Scalar(bSledovaniBarvy[0] - 10, 100, 100), new Scalar(bSledovaniBarvy[0] + 10, 255, 255), mHSVMat);

                // erodovani pro zrychleni vysledku a vylepseni zobrazeni na ostych rozzch
                Imgproc.erode(mHSVMat, mHSVMat, mErodeKernel);
                contours.clear();
                Imgproc.findContours(mHSVMat, contours, mContours, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                for (int x = 0; x < contours.size(); x++){
                    d = Imgproc.contourArea(contours.get(x));

                    //konvertovani pixelu do MatOfPoint
                    contours.get(x).convertTo(mop2F1, CvType.CV_32FC2);

                    if (d > minOblastObrysu){
                        Imgproc.approxPolyDP(mop2F1, mop2F2, 2, true);

                        //konvertovani zpet do MatOfPoint
                        mop2F2.convertTo(contours.get(x), CvType.CV_32S);

                        //vykresleni obrysu
                        Imgproc.drawContours(mRgba, contours, x, colorGreen, sirkaCar);
                    }
                }

                if (bDisplayNazev){
                    ShowTitle("Obtazeni obrysu", 1, colorYellow);
                }

                break;

            case VIEW_MODE_FACEDETECT:

                //konverze obrazu do odstinu sedi
                Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGBA2GRAY);

                rDestin.x = mRgba.width()-150;
                rDestin.y = 500;
                rDestin.width = 150;
                rDestin.height = 150;

                mFaceDest = mRgba.submat(rDestin);
                maxVyskaObliceje = 0;
                maxVyskaOblicejeIndex = -1;

                if (mCascadeClassifier != null){
                    int height1 = mGray.rows();
                    double faceSize = (double) height1 * 0.25;

                    size.width = faceSize;
                    size.height = faceSize;

                    mCascadeClassifier.detectMultiScale(mGray, morFaces, 1.1, 2, 2, size, new Size());
                    Rect[] rectsFaces = morFaces.toArray();

                    for (int i = 0; i < rectsFaces.length; i++){
                        //Vykresleni ctverce okolo nalezeneho obliceje
                        Imgproc.rectangle(mRgba, rectsFaces[i].tl(), rectsFaces[i].br(), colorRed, 3);

                        if (maxVyskaObliceje < rectsFaces[i].height){
                            maxVyskaObliceje = rectsFaces[i].height;
                            maxVyskaOblicejeIndex = i;
                        }
                    }

                    //ulozeni nejvetsiho obliceje do souboru
                    if (maxVyskaObliceje > 0){
                        rect = rectsFaces[maxVyskaOblicejeIndex];

                        //ziskani obdelniku s oblicejem
                        mROIMat = mRgba.submat(rect);

                        if (bPrvniOblicejUlozen == false){
                            SaveImage(mROIMat);
                            bPrvniOblicejUlozen = true;
                        }

                        //zmena rozmeru obliceje na 100x100px
                        size.width = 150;
                        size.height = 150;
                        Imgproc.resize(mROIMat, mFaceResized, size);

                        mFaceResized.copyTo(mFaceDest);

                        //porovnani histogramu obliceje s novym, pokud je shoda vetsi nez 0.9, je velka pravdepodobnost
                        //ze se jedna o stejny oblicej, ten se proto neulozi
                        //matFaceHistogramThis.copyTo(matFaceHistogramPrevious);
                       // matFaceHistogramThis = getHistogram(mFaceDest);
/**
                        if (matFaceHistogramThis.width() == matFaceHistogramPrevious.width()){
                            d = Imgproc.compareHist(matFaceHistogramThis, matFaceHistogramPrevious, Imgproc.CV_COMP_CORREL);
                            if (d < 0.95){
                                SaveImage(mROIMat);
                            }
                        }**/
                    }
                }

                if (bDisplayNazev){
                    ShowTitle("Detekce obliceje", 1 , colorYellow);
                }
                break;
            case VIEW_MODE_PIXEL:

               // Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGBA2GRAY);



                Imgproc.resize(mRgba, mIntermediateMat, size,0.13,0.13, Imgproc.INTER_NEAREST);
                Imgproc.resize(mIntermediateMat, mRgba, mRgba.size(), 0., 0., Imgproc.INTER_NEAREST);


                if (bDisplayNazev){
                    ShowTitle("Pixelizace", 1 , colorYellow);
                }
                break;

            //funkce pro zobrazeni objektu, ktere jsou vhodne pro sledovani
            case VIEW_MODE_GFTT:

                Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGB2GRAY);

                Imgproc.goodFeaturesToTrack(mGray, mopCorners, gFFTMax, 0.01, 20);

                y = mopCorners.rows();

                pCorners = mopCorners.toList();

                for (int x = 0; x < y; x++){
                    Imgproc.circle(mRgba, pCorners.get(x), 7, colorGreen, sirkaCar - 1);
                    NamalujStred(mRgba, colorRed, pCorners.get(x));
                }
                if (bDisplayNazev){
                    ShowTitle("Good feature to track", 1 , colorYellow);
                }
                break;









        }

        // get the time now in every frame
        nynejsiMilis = System.currentTimeMillis();

        // update the frame counter
        fps++;

        if (bDisplayNazev) {
            text = String.format("FPS: %2.1f", (float)(fps * 1000) / (float)(nynejsiMilis - pocatekMilis));
            ShowTitle (text, 2, colorYellow);
        }

        if (bSnimek) {
            // get the time of the attempt to save a screenshot
            casSnimkuMilis = System.currentTimeMillis();
            bSnimek = false;

            // try it, and set the screen text accordingly.
            // this text is shown at the end of each frame until
            // 1.5 seconds has elapsed
            if (SaveImage (mRgba)) {
                textShot = "Snimek obrazovky ulozen";
            }
            else {
                text = "Chyba ukladani snimku obrazovky";
            }

        }

        if (System.currentTimeMillis() - casSnimkuMilis < 1500)
            ShowTitle (textShot, 3, colorRed);
        return mRgba;
    }

    public Mat getHistogram(Mat mat) {
        Imgproc.calcHist(Arrays.asList(mat), moiOne, new Mat(), mHist, moiSize, mofRange);

        Core.normalize(mHist, mHist);

        return mHist;
    }


    public boolean onTouchEvent(final MotionEvent motionEvent){

        bSnimek = true;
        return false;
    }

    private void NamalujStred(Mat mat, Scalar color, Point pt) {
        //metoda pro vykresleni stredu kruhu
        int velikost = 24;

        pt1.x = pt.x - (velikost >> 1);
        pt1.y = pt.y;
        pt2.x = pt.x + (velikost >> 1);
        pt2.y = pt.y;

        Imgproc.line(mat, pt1, pt2, color, sirkaCar - 1);

        pt1.x = pt.x;
        pt1.y = pt.y + (velikost >> 1);
        pt2.x = pt.x;
        pt2.y = pt.y - (velikost >> 1);

        Imgproc.line(mat, pt1, pt2, color, sirkaCar -1);
    }

    private boolean SaveImage(Mat mat) {
        //ulozeni snimku obrazovky ze zobrazovaci matice

        Imgproc.cvtColor(mat, mIntermediateMat, Imgproc.COLOR_RGB2BGR, 3);

        File cesta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String filename = "FrameworkCV_";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date(System.currentTimeMillis());
        String stringDate = simpleDateFormat.format(date);
        filename += stringDate + "-" + poradiSouboru;
        filename += ".png";

        File file = new File(cesta, filename);

        Boolean aBoolean = null;
        filename = file.toString();
        aBoolean = Imgcodecs.imwrite(filename, mIntermediateMat);

        return aBoolean;

    }
    private void ShowTitle(String s, int pocetUsecek, Scalar color) {
        Imgproc.putText(mRgba,s, new Point(10, (int)(dVelikostTextu *60 * pocetUsecek)),Core.FONT_HERSHEY_COMPLEX, dVelikostTextu,color,1);
    }

    public void onPause(){
        super.onPause();
        if (javaCameraView1 != null)
            javaCameraView1.disableView();
        if (pocetKamer > 1);
        if (javaCameraView2 != null)
            javaCameraView2.disableView();
        if (thread!=null){
            thread.interrupt();
        }
    }

    public void onResume(){
        super.onResume();
        viewMode = VIEW_MODE_RGBA;


        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, baseLoaderCallback);
    }

   /** @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position){
            case 0:
                Toast.makeText(parent.getContext(), "Obdelnikova eroze", Toast.LENGTH_SHORT).show();
                mErodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size3);
                break;
            case 1:
                Toast.makeText(parent.getContext(), "Křížová eroze", Toast.LENGTH_SHORT).show();
                mErodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, size3);
                break;
            case 2:
                Toast.makeText(parent.getContext(), "Elipsová eroze", Toast.LENGTH_SHORT).show();
                mErodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, size3);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        mErodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, size3);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {

        switch (itemPosition){
            case 0:

                mErodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size3);
                break;
            case 1:
                mErodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, size3);
                break;
            case 2:
                mErodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, size3);
                break;
        }
        return true;
    }
    **/
}

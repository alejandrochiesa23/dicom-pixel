package fxml.controller;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.sarxos.webcam.Webcam;
import javafx.scene.control.ProgressBar;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.control.Slider;

public class TaskCamera extends Task<Void> {

	BufferedImage[] listImg;
	public boolean stopCamera = false;
	BufferedImage grabbedImage;
	Webcam webcamDefault;
	BufferedImage //
	[] // CapturedImage
	arrayImg;
	private BufferedImage bwImage;
	private BufferedImage bwImage2;
	ObjectProperty<Image> imageProperty;
	ObjectProperty<Image> imageProperty2;
	ObjectProperty<Image> imageProperty3;
	ObjectProperty<Image> miniFrame;
	private WebCamPreviewController webCamPreviewController;

	public TaskCamera(boolean stopCam, BufferedImage grabbedImage, Webcam webcamDefault, CapturedImage[] arrayImg,
			ObjectProperty<Image> imageProperty, ObjectProperty<Image> imageProperty2,
			ObjectProperty<Image> imageProperty3, ObjectProperty<Image> miniFrame,
			WebCamPreviewController webCamPreviewController) {
		this.stopCamera = stopCam;
		this.grabbedImage = grabbedImage;
		this.webcamDefault = webcamDefault;
		this.imageProperty = imageProperty;
		this.imageProperty2 = imageProperty2;
		this.imageProperty3 = imageProperty3;
		this.miniFrame = miniFrame;
		this.webCamPreviewController = webCamPreviewController;
		// this.arrayImg = arrayImg;

	}

	@Override
	protected Void call() throws Exception {

		ArrayDeque<BufferedImage> imagenes = new ArrayDeque<BufferedImage>(1000);

		// int nThreads = Runtime.getRuntime().availableProcessors();
		// System.out.println("procesadores: " + nThreads);
		ExecutorService executor = Executors.newSingleThreadExecutor();

		// SimpleDateFormat sdf = new SimpleDateFormat("ss");
		ToGray tg = new ToGray();
		String segundo = "";
		SimpleDateFormat ssdf = new SimpleDateFormat("ss");
		Calendar inicio, fin;
		double value;
		while (!stopCamera) {
			try {
				if ((grabbedImage = webcamDefault.getImage()) != null) {
					inicio = Calendar.getInstance();
					arrayImg = tg.convertBufferedImage(grabbedImage, imagenes);
					fin = Calendar.getInstance();
					if (!ssdf.format(new Date()).equals(segundo)) {
						segundo = ssdf.format(new Date());
						value = ((double) 1 / (fin.getTimeInMillis() - inicio.getTimeInMillis()) * 1000);
						updateMessage(String.valueOf(value).substring(0, String.valueOf(value).indexOf(".")));
						inicio = fin;
					}

					bwImage = arrayImg[0];
					bwImage2 = arrayImg[1];

					executor.submit(new Runnable() {
						// @Override
						public void run() {
							final Image mainiamge = SwingFXUtils.toFXImage(grabbedImage, null);
							final Image mainiamge2 = SwingFXUtils.toFXImage(bwImage, null);
							final Image mainiamge3 = SwingFXUtils.toFXImage(bwImage2, null);
							imageProperty.set(mainiamge);
							imageProperty2.set(mainiamge2);
							imageProperty3.set(mainiamge3);
						}
					});

					if (imagenes.size() == 60) {
						stopCamera = true;
						this.webCamPreviewController.stopCamera(null);
						// this.webCamPreviewController.btnStopCamera.setDisable(false);
						miniFrame.set(SwingFXUtils.toFXImage(imagenes.poll(), null));
					}
				}
			} catch (Exception e) {
			}
		}

		webCamPreviewController.createSlider(imagenes);
		
		// TaskCamera.saveImages(imagenes);
		return null;
	}

	protected Image getImageNum(Number new_val) {
		SwingFXUtils.toFXImage(listImg[new_val.intValue()], null);
		return null;
	}

	// public static void saveImages(ArrayDeque<BufferedImage> imagenes) {
	// try {
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hh_mm__ss__SSS");
	//
	// if (imagenes.size() == 200) {
	// System.out.println("guardando...");
	// TaskCamera.timeEnds(Calendar.getInstance().getTime().getTime());
	// for (int i = 0; i < imagenes.size(); i++) {
	// CapturedImage capImg = (CapturedImage)imagenes.poll();
	//
	// String fileOutput = "imgs/out" + sdf.format(capImg.getTime()) + ".gif";
	// File outputPhoto = new File(fileOutput);
	// ImageIO.write(capImg, "gif", outputPhoto);
	//
	//
	// }
	// }
	//
	// } catch (IOException e) {
	// e.printStackTrace();
	//
	// }
	// }

};

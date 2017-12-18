/*
 *   OpenCASA software v0.8 for video and image analysis
 *   Copyright (C) 2017  Carlos Alquézar
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/    

package gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import data.Cell;
import data.Params;
import functions.ComputerVision;
import functions.FileManager;
import functions.Paint;
import functions.VideoRecognition;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;

public class ViabilityWindow extends ImageAnalysisWindow implements ChangeListener, MouseListener {

  private enum Channel {
    BLUE, GREEN, RED, NONE
  }
  private Channel channel = Channel.NONE;
  private ImagePlus            aliveImpOutline;
  protected List<Cell> aliveSpermatozoa = new ArrayList<Cell>();
  private ImagePlus            deadImpOutline;
  protected List<Cell> deadSpermatozoa  = new ArrayList<Cell>();
  private boolean              isThresholding   = false;
  private boolean forceChannelNone = true; 
  private boolean			   isProcessing = false;
  private ResultsTable results = new ResultsTable();

  private int totalCells = 0;
  private int nViableCells = 0;
  private int nNonViableCells = 0;
  
  /**
   * Constructor
   */
  public ViabilityWindow() {
    super();
    sldRedThreshold.setVisible(true);
    sldGreenThreshold.setVisible(true);
    sldRedThreshold.addMouseListener(this);
    sldGreenThreshold.addMouseListener(this);
    sldBlueThreshold.addMouseListener(this);
    setChangeListener(this,sldRedThreshold);
    setChangeListener(this,sldGreenThreshold);
    setMouseListener(this);
//    btnOtsu.setVisible(false);
//    btnMinimum.setVisible(false);
    nextBtn.setText("Save and Next Image");
    setGenericLabels();
    results.showRowNumbers(false);

  }
  /**
   * This method refreshes the showed image after changing the threshold with
   * the sliderbar
   */
  private void doSliderRefresh() {
    if (!isThresholding) {
      isThresholding = true;
      Thread t1 = new Thread(new Runnable() {
        public void run() {
          processImage(true);
          isThresholding = false;
        }
      });
      t1.start();
    }
  }

  protected void drawImage() {
    // Draw cells on image
    impDraw = impOrig.duplicate();
    Paint paint = new Paint();
    if(channel==Channel.GREEN){
      paint.drawOutline(impDraw, aliveImpOutline);
      impDraw.setColor(Color.green);
      paint.drawBoundaries(impDraw, aliveSpermatozoa);
    }else if(channel==Channel.RED){
      paint.drawOutline(impDraw, deadImpOutline);
      impDraw.setColor(Color.red);
      paint.drawBoundaries(impDraw, deadSpermatozoa);
    }else if(channel==Channel.BLUE){
      //Not used in this module version
    }else if(channel==Channel.NONE){
      paint.drawOutline(impDraw, aliveImpOutline);
      impDraw.setColor(Color.green);
      paint.drawBoundaries(impDraw, aliveSpermatozoa);
      paint.drawOutline(impDraw, deadImpOutline);
      impDraw.setColor(Color.red);
      paint.drawBoundaries(impDraw, deadSpermatozoa);
    }
    nViableCells = aliveSpermatozoa.size();
    nNonViableCells = deadSpermatozoa.size();
    totalCells = nViableCells + nNonViableCells;
    setGenericLabels();
    setImage();
  }

  private void setGenericLabels(){
    genericLabel1.setText("Total cells: "+totalCells);
    genericLabel2.setText("Viable cells: "+nViableCells);
    genericLabel3.setText("Non-viable cells: "+nNonViableCells);
  }
  
  private void generateResults() {

    int aliveCount = aliveSpermatozoa.size();
    int deadCount = deadSpermatozoa.size();
    results.incrementCounter();
    results.addValue("Alives", aliveCount);
    results.addValue("Deads", deadCount);
    int total = aliveCount + deadCount;
    results.addValue("Total", total);
    float percAlives = ((float) aliveCount) / ((float) total) * 100;
    results.addValue("% Alives", percAlives);
    results.addValue("% Deads", 100-percAlives);
    FileManager fm = new FileManager();
    results.addValue("Sample", fm.getParentDirectory(impOrig.getTitle()));
    results.addValue("Filename", fm.getFilename(impOrig.getTitle()));
    if (!Params.male.isEmpty())
      results.addValue("Male", Params.male);
    if (!Params.date.isEmpty())
      results.addValue("Date", Params.date);
    if (!Params.genericField.isEmpty())
      results.addValue("Generic Field", Params.genericField);
    results.show("Viability results");

  }

  private List<Cell> getSpermatozoa(Channel rgbChannel) {
    ComputerVision cv = new ComputerVision();

    if (rgbChannel == Channel.RED){
      impTh = cv.getRedChannel(impOrig.duplicate());
      if(threshold!=-1)
        threshold = redThreshold;
    }
    else if (rgbChannel == Channel.GREEN){
      impTh = cv.getGreenChannel(impOrig.duplicate());
      if(threshold!=-1)
        threshold = greenThreshold;
    }
    else if (rgbChannel == Channel.BLUE){
      impTh = cv.getBlueChannel(impOrig.duplicate());
      if(threshold!=-1)
        threshold = blueThreshold;
    }
   
    cv.convertToGrayscale(impTh);
    thresholdImagePlus(impTh);
    // this will be useful for painting outlines later
    if (rgbChannel == Channel.RED)
      deadImpOutline = impTh;
    else if (rgbChannel == Channel.GREEN)
      aliveImpOutline = impTh;
    else if (rgbChannel == Channel.BLUE)
      aliveImpOutline = impTh;
    VideoRecognition vr = new VideoRecognition();
    List<Cell>[] sperm = vr.detectCells(impTh);
    return sperm[0];
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
    setRawImage();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    channel = Channel.NONE;
    if(!isThresholding)
    	drawImage();
  }
  
  protected void previousAction(){
    forceChannelNone=true;
  }
  protected void nextAction() {
    forceChannelNone=true;
    generateResults();
  }
  

  protected void processImage(boolean eventType) {
    // In this module, eventType is not used
    if(!isProcessing){//else do not disturb
      isProcessing = true;
  	  aliveSpermatozoa = getSpermatozoa(Channel.GREEN);
      deadSpermatozoa = getSpermatozoa(Channel.RED);
      if(aliveSpermatozoa != null){
      	spermatozoa = new ArrayList<Cell>(aliveSpermatozoa);
      	if(deadSpermatozoa != null){
      		spermatozoa.addAll(deadSpermatozoa);
      	}
      } else if(deadSpermatozoa != null){
      	spermatozoa = new ArrayList<Cell>(deadSpermatozoa);
      }else{
      	spermatozoa = new ArrayList<Cell>();
      }
      selectAll();// set as selected all spermatozoa to allow boundary painting
      idenfitySperm();
      // Calculate outlines
      ComputerVision cv = new ComputerVision();
      cv.outlineThresholdImage(aliveImpOutline);
      cv.outlineThresholdImage(deadImpOutline);
      if(forceChannelNone){
        channel = Channel.NONE;
        forceChannelNone=false;
      }
      drawImage();
      isProcessing = false;
    }
  }

  protected void genericRadioButtonsAction(){
    forceChannelNone=true;
  }
  
  @Override
  public void stateChanged(ChangeEvent e) {
    Object auxWho = e.getSource();
    if ((auxWho == sldRedThreshold)) {
      channel = Channel.RED;
      redThreshold = sldRedThreshold.getValue();
      doSliderRefresh();
    }
    else if ((auxWho == sldGreenThreshold)) {
      channel = Channel.GREEN;
      // Updating threshold value from slider
      greenThreshold = sldGreenThreshold.getValue();
      doSliderRefresh();
    }else if(auxWho == sldBlueThreshold){
      channel = Channel.BLUE;
      blueThreshold = sldBlueThreshold.getValue();
      doSliderRefresh();
    }
  }

}

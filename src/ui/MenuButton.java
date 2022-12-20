package ui;

import gamestates.Gamestate;
import utils.LoadSave;
import static utils.Constants.UI.Buttons.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MenuButton {
    private int xPos, yPos, rowIndex, index;
    private int xOffsetCenter = B_WIDTH/2;
    private Gamestate state;
    private BufferedImage[] imgs;
    private boolean isMouseOver, isMousePressed;
    private Rectangle bounds;




    public MenuButton(int xPos, int yPos, int rowIndex, Gamestate state){
        this.xPos = xPos;
        this.yPos = yPos;
        this.rowIndex = rowIndex;
        this.state = state;
        loadImgs();
        initBounds();

    }

    private void initBounds() {
        bounds = new Rectangle(xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT);
    }

    public Rectangle getBounds(){
        return bounds;
    }

    private void loadImgs() {
        imgs = new BufferedImage[3];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);
        for(int i = 0; i < imgs.length; i++){
            imgs[i] = temp.getSubimage(i * B_WIDTH_DEFAULT, rowIndex * B_HEIGHT_DEFAULT, B_WIDTH_DEFAULT, B_HEIGHT_DEFAULT);
        }
    }

    public void draw(Graphics g){
        g.drawImage(imgs[index], xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT, null);
    }

    public void update(){
        index = 0;
        if(isMouseOver){
            index = 1;
        }
        if (isMousePressed){
            index = 2;
        }
    }

    public boolean isMouseOver() {
        return isMouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        isMouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return isMousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        isMousePressed = mousePressed;
    }

    public void applyGamestate(){
        Gamestate.state = state;
    }

    public void resetBools(){
        isMouseOver = false;
        isMousePressed = false;
    }

}

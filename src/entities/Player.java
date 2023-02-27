package entities;

import gamestates.Playing;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.*;
import static utils.Constants.*;

public class Player extends Entity{
    private BufferedImage[][] animations;
    private boolean isMoving;

    private boolean isAttacking = false;
    private boolean left, right;
    private boolean jump;


    private int[][] lvlData;

    private float xDrawOffset = 21 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;

    private float jumpSpeed = -2.25f * Game.SCALE;

    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

    //StatusBar
    private BufferedImage statusBarImg;

    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    private int statusBarX = (int) (10 * Game.SCALE);
    private int statusBarY = (int) (10 * Game.SCALE);

    private int healthBarWidth = (int) (150 * Game.SCALE);
    private int healthBarHeight = (int) (4 * Game.SCALE);
    private int healthBarXStart = (int) (34 * Game.SCALE);
    private int healthBarYStart = (int) (14 * Game.SCALE);


    private int healthWidth = healthBarWidth;

    private int flipX = 0;
    private int flipW = 1;

    private boolean attackChecked;
    private Playing playing;

    public Player (float x, float y, int width, int height, Playing playing){
        super(x, y, width, height);
        this.playing = playing;
        this.walkSpeed = 1 * Game.SCALE;
        this.state = IDLE;

        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.loadAnimations();
        initHitBox(20, 27);
        initAttackBox();
    }

    public void setSpawn(Point spawn){
        this.x = spawn.x;
        this.y = spawn.y;
        hitBox.x = x;
        hitBox.y = y;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int)(20 * Game.SCALE), (int)(20 * Game.SCALE));
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void update(){
        updateHealthBar();
        if(currentHealth <= 0) {
            playing.setGameOver(true);
            return;
        }


        updateAttackBox();
        updatePos();

        if(isMoving) {
            checkPotionTouched();
            checkSpikesTouched();
        }
        if(isAttacking){
            checkAttack();
        }
        updateAnimationTick();
        setAnimation();

    }

    private void checkSpikesTouched() {
        playing.checkSpikesTouched();
    }

    private void checkPotionTouched() {
        playing.checkPotionTouched(hitBox);
    }

    private void checkAttack() {
        if(attackChecked || animIndex != 1)
            return;
        attackChecked = true;
        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
    }

    private void updateAttackBox() {
        if(right){
            attackBox.x = hitBox.x + hitBox.width + (int)(Game.SCALE * 10);
        }else if (left){
            attackBox.x = hitBox.x - hitBox.width - (int)(Game.SCALE * 10);
        }
        attackBox.y = hitBox.y + (Game.SCALE * 10);
    }

    private void updateHealthBar(){
        healthWidth = (int) ((currentHealth / (float)maxHealth) * healthBarWidth);
    }

    public void render(Graphics g, int lvlOffset){
        g.drawImage(
                animations[state][animIndex],
                (int)(hitBox.x - xDrawOffset) - lvlOffset + flipX,
                (int)(hitBox.y - yDrawOffset) ,
                width * flipW,
                height,
                null);
        drawHitBox(g, lvlOffset);
        drawAttackBox(g, lvlOffset);
        drawUI(g);
    }



    private void drawUI(Graphics g) {
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.RED);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
    }

    public void setAttacking(boolean attacking) {
        this.isAttacking = attacking;
    }

    private void setAnimation(){

        int startAnim = state;
        state = isMoving ? RUNNING: IDLE;

        state = inAir ? airSpeed < 0 ? JUMP : FALLING : state;

        state = isAttacking ? ATTACK : state;
        if(isAttacking)
            if(startAnim != ATTACK)
            {
                animIndex = 1;
                animTick = 0;
                return;
            }

        
        if (startAnim != state){
            resetAnimTick();
        }
    }

    private void resetAnimTick() {
        animTick = 0;
        animIndex = 0;
    }

    private void updatePos(){
        isMoving = false;

        if(jump)
            jump();

        if(!inAir)
            if((!left && !right) || (right && left))
                return;

        float xSpeed = 0;

        if (left){
            xSpeed -= walkSpeed;
            flipX = width;
            flipW = -1;
        }


        if (right){
            xSpeed += walkSpeed;
            flipX = 0;
            flipW = 1;
        }


        if(!inAir)
            if(!IsEntityOnFloor(hitBox, lvlData))
                inAir = true;

        if(inAir){
            if(CanMoveHere(hitBox.x, hitBox.y + airSpeed, hitBox.width, hitBox.height, lvlData)){
                hitBox.y += airSpeed;
                airSpeed += GRAVITY;
                updateXPos(xSpeed);
            }
            else{
                hitBox.y = GetEntityYPosUnderRoofOrAboveFloor(hitBox, airSpeed);
                if (airSpeed > 0){
                    resetInAir();
                }
                else{
                    airSpeed = fallSpeedAfterCollision;
                }
                updateXPos(xSpeed);
            }
        }else{
            updateXPos(xSpeed);
        }

        isMoving = true;


    }

    private void jump() {
        if(inAir){
            return;
        }
        inAir = true;
        airSpeed += jumpSpeed;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }


    private void updateXPos(float xSpeed) {
        if(CanMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvlData)){
            hitBox.x += xSpeed;
            isMoving = true;
        }
        else{
            hitBox.x = GetEntityXPosNextToWall(hitBox, xSpeed);
        }
    }

    public void changePower(int value){
        System.out.println("Added Power!");
    }

    public void changeHealth(int value){
        currentHealth += value;
        if(currentHealth <= 0){
            currentHealth = 0;
            //GameOver();
        } else if (currentHealth >= maxHealth){
            currentHealth = maxHealth;
        }
    }


    private void updateAnimationTick(){
        animTick++;
        if(animTick >= ANI_SPEED){
            animTick = 0;
            animIndex++;
            if (animIndex == GetSpriteAmount(state)){
                isAttacking = false;
                animIndex = 0;
                attackChecked = false;
            }
            animIndex = animIndex % GetSpriteAmount(state);
        }
    }

    private void loadAnimations(){
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

        animations = new BufferedImage[7][8];
        for (int j=0; j< animations.length; j++)
            for (int i=0; i <animations[j].length; i++){
                animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40);
            }

        statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
    }

    public void loadLvlData(int[][] lvlData){
        this.lvlData = lvlData;
        if(!IsEntityOnFloor(hitBox, lvlData))
        {
            inAir = true;
        }
    }

    public void resetDirBooleans(){
        left = false;
        right = false;
    }


    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void resetAll() {
        resetDirBooleans();
        inAir = false;
        isAttacking = false;
        isMoving = false;
        state = IDLE;
        currentHealth = maxHealth;

        hitBox.x = x;
        hitBox.y = y;

        if(!IsEntityOnFloor(hitBox, lvlData))
            inAir = true;
    }

    public void kill() {
        currentHealth = 0;
    }
}

package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 3/29/2016.
 */

public class Archer extends ChessPiece {

    enum ArcherState {
        M_IDLE,
        M_TURNINGCW,
        M_TURNINGCCW,
        M_TURNINGBACKWARDS,
        M_MOVING,
        A_IDLE,
        A_SHOOT,
        A_SIDESTEP_L,
        A_SIDESTEP_R,
        READY,
        UNREADY,
        DYING
    }

    Weapon weapon;

    ArcherState state;

    Animation aReady, aUnready, aReadyIdle, aReadySidestepL, aReadySidestepR;
    Sound sMove, sArrowHit, sDie, sChangeColor, sTurn, sReady, sCharged;

    int numArrows = 3;

    Archer(int x, int y) {
        super("Player", PieceDirection.UP, PieceType.ENTITY, x, y);

        this.weapon = new Weapon("Crossbow");

        addChild(this.weapon);
        // Fix blender rotation
        this.rotation = new float[]{270, 0f, 0f};
        // Link child rot and pos
        this.weapon.rotation = this.rotation;
        this.weapon.position = this.position;
        // Load sounds
        /*this.sMove = loadSound(R.raw.rock_scrape);
        this.sArrowHit = loadSound(R.raw.arrow_hit);
        this.sDie = loadSound(R.raw.bell3);
        this.sChangeColor = loadSound(R.raw.splash);
        this.sTurn = loadSound(R.raw.rock_scrape2);
        this.sReady = loadSound(R.raw.rock_scrape3);
        this.sCharged = loadSound(R.raw.bow_draw);*/
        // Load animations
        this.loadAnimations();
        // Set starting stance based on game mode
        if(GameConstants.gameMode == GameConstants.GameMode.ADVENTURE) {
            this.idle();
        }else if(GameConstants.gameMode == GameConstants.GameMode.SURVIVAL){
            this.ready();
        }
        // Set camera on
        super.moveCameraTo();
        // Set current weapon color
        this.weaponColor = WeaponColor.RED;
    }

    @Override
    protected void update() {
        super.update();
        // Change time modifier using private function
        this.changeSpeed();
        //  Handle states
        if (this.state == ArcherState.M_IDLE) {
            this.idle();
        } else if (this.state == ArcherState.A_IDLE) {
            this.readyidle();
        } else if (this.canMove()) {
            if (this.state == ArcherState.DYING) {
                return;
            } else if (this.state == ArcherState.M_MOVING) {
                super.ActionUp();
                this.idle();
            } else if (this.state == ArcherState.M_TURNINGBACKWARDS) {
                super.ActionLeft();
                super.moveCameraTo();
                this.ActionLeft();
            } else if (this.state == ArcherState.M_TURNINGCW) {
                super.ActionLeft();
                super.moveCameraTo();
                // Set idle in case cannot move forward; will set state to idle
                this.idle();
                // Move forward
                this.ActionUp();
            } else if (this.state == ArcherState.M_TURNINGCCW) {
                super.ActionRight();
                super.moveCameraTo();
                // Set idle in case cannot move forward; will set state to idle
                this.idle();
                // Move forward
                this.ActionUp();
            } else if (this.state == ArcherState.READY) {
                this.readyidle();
            } else if (this.state == ArcherState.UNREADY) {
                this.idle();
            } else if (this.state == ArcherState.A_SHOOT) {
                this.readyidle();
            } else if (this.state == ArcherState.A_SIDESTEP_L) {
                super.ActionRight();
                super.ActionUp();
                super.ActionLeft();
                this.readyidle();
            } else if (this.state == ArcherState.A_SIDESTEP_R) {
                super.ActionLeft();
                super.ActionUp();
                super.ActionRight();
                this.readyidle();
            }
        }
        //  Handle controller tasks
        if (super.hasTask()) {
            PieceTask currentTask = tasks.get(0);
            if (this.canMove()) {
                tasks.remove(0);
                if (currentTask == PieceTask.UP) {
                    if (this.direction == PieceDirection.UP) {
                        this.ActionUp();
                    } else if (this.direction == PieceDirection.LEFT) {
                        this.ActionLeft();
                    } else if (this.direction == PieceDirection.RIGHT) {
                        this.ActionRight();
                    } else {
                        this.ActionDown();
                    }
                } else if (currentTask == PieceTask.DOWN) {
                    if (this.direction == PieceDirection.DOWN) {
                        this.ActionUp();
                    } else if (this.direction == PieceDirection.RIGHT) {
                        this.ActionLeft();
                    } else if (this.direction == PieceDirection.LEFT) {
                        this.ActionRight();
                    } else {
                        this.ActionDown();
                    }
                } else if (currentTask == PieceTask.RIGHT) {
                    if (this.direction == PieceDirection.RIGHT) {
                        this.ActionUp();
                    } else if (this.direction == PieceDirection.UP) {
                        this.ActionLeft();
                    } else if (this.direction == PieceDirection.DOWN) {
                        this.ActionRight();
                    } else {
                        this.ActionDown();
                    }
                } else if (currentTask == PieceTask.LEFT) {
                    if (this.direction == PieceDirection.LEFT) {
                        this.ActionUp();
                    } else if (this.direction == PieceDirection.DOWN) {
                        this.ActionLeft();
                    } else if (this.direction == PieceDirection.UP) {
                        this.ActionRight();
                    } else {
                        this.ActionDown();
                    }
                } else if (currentTask == PieceTask.ACTION) {
                    if (this.state == ArcherState.M_IDLE) {
                        this.ready();
                    } else if (this.state == ArcherState.A_IDLE) {
                        this.attack();
                    }
                }
            }
        }
        /*if (taskOverflow != null) {
            PieceTask task = super.taskOverflow;
            super.taskOverflow = null;
            if (task == PieceTask.ACTION) {
                this.changeColorForward();
            }
        }*/
        //  Update models with current frame
        this.updateMesh();
        // Handle frame events
        //  Frame events happen mid-animation
        String[] animTasks = super.getAnimationEvents();
        if (animTasks != null) {
            for (String eventName : animTasks) {
                switch (eventName) {
                    case ("move_forward"):
                        // Move forward on board and move camera
                        super.moveForwardOnBoard();
                        super.moveCameraTo();
                        break;
                    case ("sidestep_l"):
                        // Turn left, move forward, and turn right
                        super.ActionRight();
                        super.moveForwardOnBoard();
                        super.ActionLeft();
                        if (GameConstants.gameMode == GameConstants.GameMode.ADVENTURE) {
                            super.moveCameraTo();
                        }
                        break;
                    case ("sidestep_r"):
                        // Turn right, move forward, turn left
                        super.ActionLeft();
                        super.moveForwardOnBoard();
                        super.ActionRight();
                        if (GameConstants.gameMode == GameConstants.GameMode.ADVENTURE) {
                            super.moveCameraTo();
                        }
                        break;
                    case ("shoot"):
                        int[] pos = this.tilePos;
                        // Check if charged shot

                        ChessPiece gotPiece = GameConstants.tileMap.getAcrossPiece(this);
                        pos = gotPiece.tilePos;
                        // Handle objects that do not matter
                        while (gotPiece.TAG.equals("Barricade")) {
                            // Buffer rotation
                            PieceDirection savedDir = gotPiece.direction;
                            // Change rotation to archer's rotation
                            gotPiece.direction = this.direction;
                            // Use new rotation to get across piece
                            ChessPiece newPiece = GameConstants.tileMap.getAcrossPiece(gotPiece);
                            // Put back original cameraDirection
                            gotPiece.direction = savedDir;
                            // If is null, use last across piece
                            if (newPiece == null) {
                                break;
                            }
                            // Else, use new piece and see if can interact
                            gotPiece = newPiece;
                            pos = gotPiece.tilePos;
                        }
                        if (gotPiece.TAG.equals("CubeMonster")) {
                            // Interact with entity
                            CubeMonster monster = (CubeMonster) gotPiece;
                            if (this.weaponColor == monster.weaponColor) {
                                gotPiece.remove(this);
                            } else {
                                // Leave arrow on ground from bouncing off monster
                                if (this.direction == PieceDirection.UP) {
                                    pos = GameConstants.tileMap.getSouthTile(pos[0], pos[1]).position;
                                } else if (this.direction == PieceDirection.DOWN) {
                                    pos = GameConstants.tileMap.getNorthTile(pos[0], pos[1]).position;
                                } else if (this.direction == PieceDirection.LEFT) {
                                    pos = GameConstants.tileMap.getEastTile(pos[0], pos[1]).position;
                                } else {
                                    pos = GameConstants.tileMap.getWestTile(pos[0], pos[1]).position;
                                }
                            }
                        } else {
                            // Leave arrow on ground from bouncing off wall
                            if (this.direction == PieceDirection.UP) {
                                pos = GameConstants.tileMap.getSouthTile(pos[0], pos[1]).position;
                            } else if (this.direction == PieceDirection.DOWN) {
                                pos = GameConstants.tileMap.getNorthTile(pos[0], pos[1]).position;
                            } else if (this.direction == PieceDirection.LEFT) {
                                pos = GameConstants.tileMap.getEastTile(pos[0], pos[1]).position;
                            } else {
                                pos = GameConstants.tileMap.getWestTile(pos[0], pos[1]).position;
                            }
                        }
                        // Check if drop arrow on floor at last checked pos
                        if (GameConstants.Drop_Arrows) {
                            Arrow arrow = new Arrow("Bolt", "Color", pos[0], pos[1]);
                            arrow.randomizeRotation(1, 20);
                            GameConstants.tileMap.addItem(arrow);
                        }
                        // Play arrow sound
                        this.sArrowHit.play();
                        // Check if should de-increment arrow count
                        if (GameConstants.Lose_Arrows) {
                            this.numArrows--;
                        }
                        // Shake camera
                        GameConstants.camera.startShake(75);
                        break;
                    default:
                        LOGE("Unhandled animationevent " + eventName);
                        break;

                }
            }
        }
        // Handle block sitting
        Tile tile = GameConstants.tileMap.map[this.tilePos[1]][this.tilePos[0]];
        ChessPiece[] items = tile.getItems();
        for (ChessPiece item : items) {
            if (item.TAG.equals("Bolt")) {
                GameConstants.tileMap.removeItem(item);
                this.numArrows++;
            }
        }
    }

    @Override
    protected void remove(ChessPiece removeBy){
        GameConstants.tileMap.addGarbage(this);
        this.state = ArcherState.DYING;
        // Resolve rotation via hit
        PieceDirection gDirection = removeBy.direction;
        PieceTask sendTask = null;
        if(gDirection == PieceDirection.LEFT){
            sendTask = PieceTask.RIGHT;
        }else if(gDirection == PieceDirection.RIGHT){
            sendTask = PieceTask.LEFT;
        }else if(gDirection == PieceDirection.UP){
            sendTask = PieceTask.DOWN;
        }else if(gDirection == PieceDirection.DOWN){
            sendTask = PieceTask.UP;
        }
        super.resolveRotationTo(sendTask);
        // Set states and animations
        super.setCurrentAnimation(super.aDeath);
        this.weapon.setCurrentAnimation(this.weapon.aDeath);
        this.play();
        // Play death noise
        this.sDie.play();
        // Shake camera
        GameConstants.camera.startShake(100);
        //Pause game
        GameConstants.easeFrameRateTo(1, 100);
    }

    @Override
    protected boolean canMove(){
        if(this.currentAnimation == this.aReadyIdle){
            return true;
        }
        return super.canMove();
    }

    private boolean attackMode(){
        if(this.currentAnimation == this.aReady || this.currentAnimation == this.aReadyIdle || this.currentAnimation == this.aAttack || this.currentAnimation == this.aReadySidestepL || this.currentAnimation == this.aReadySidestepR) {
            return true;
        }
        return false;
    }

    private void loadAnimations(){
        String idle = "idle";
        this.aIdle = quickAddAnim(idle);
        this.weapon.aIdle = this.weapon.quickAddAnim(idle);

        String move = "move";
        this.aMove = quickAddAnim(move);
        this.aMove.addAnimationEvent("move_forward", 13);
        this.weapon.aMove = this.weapon.quickAddAnim(move);

        String turnc = "turncw";
        this.aTurnCW = quickAddAnim(turnc);
        this.weapon.aTurnCW = this.weapon.quickAddAnim(turnc);

        String turncc = "turnccw";
        this.aTurnCCW = quickAddAnim(turncc);
        this.weapon.aTurnCCW = this.weapon.quickAddAnim(turncc);

        String ready = "ready";
        this.aReady = quickAddAnim(ready);
        this.weapon.aReady = this.weapon.quickAddAnim(ready);

        String unready = "unready";
        this.aUnready = quickAddAnim(unready);
        this.weapon.aUnready = this.weapon.quickAddAnim(unready);

        String readyidle = "ready_idle";
        this.aReadyIdle = quickAddAnim(readyidle);
        this.weapon.aReadyIdle = this.weapon.quickAddAnim(readyidle);

        String readyattack = "ready_shoot";
        this.aAttack = quickAddAnim(readyattack);
        this.aAttack.addAnimationEvent("shoot", 2);
        this.weapon.aAttack = this.weapon.quickAddAnim(readyattack);

        String sidestep_l = "ready_sidestep_l";
        this.aReadySidestepL = this.quickAddAnim(sidestep_l);
        this.aReadySidestepL.addAnimationEvent("sidestep_l", 12);
        this.weapon.aReadySidestepL = this.weapon.quickAddAnim(sidestep_l);

        String sidestep_r = "ready_sidestep_r";
        this.aReadySidestepR = this.quickAddAnim(sidestep_r);
        this.aReadySidestepR.addAnimationEvent("sidestep_r", 12);
        this.weapon.aReadySidestepR = this.weapon.quickAddAnim(sidestep_r);

        String death = "death";
        this.aDeath = quickAddAnim(death);
        this.weapon.aDeath = this.weapon.quickAddAnim(death);
    }

    @Override
    public void ActionUp() {
        if (this.attackMode()) {
            this.changeColorForward();
            return;
        }
        if (!super.canMoveForward()) {
            return;
        }
        this.setCurrentAnimation(aMove);
        this.weapon.setCurrentAnimation(this.weapon.aMove);
        this.play();
        this.state = ArcherState.M_MOVING;
        this.sMove.play();
    }

    @Override
    public void ActionLeft() {
        if(this.state == ArcherState.DYING){
            super.ActionLeft();
            return;
        }
        if(this.attackMode()){
            // Check left to see if can move
            super.ActionLeft();
            boolean canMoveF = super.canMoveForward();
            super.ActionRight();
            if(!canMoveF){
                return;
            }
            // Sidestep left
            this.readysidestepr();
            return;
        }
        this.setCurrentAnimation(this.aTurnCW);
        this.weapon.setCurrentAnimation(this.weapon.aTurnCW);
        this.play();
        this.state = ArcherState.M_TURNINGCW;
        this.sTurn.play();
    }

    @Override
    public void ActionRight() {
        if(this.state == ArcherState.DYING){
            super.ActionRight();
            return;
        }
        if(this.attackMode()) {
            // Check right to see if can move
            super.ActionRight();
            boolean canMoveF = super.canMoveForward();
            super.ActionLeft();
            if (!canMoveF) {
                return;
            }
            // Sidestep left
            this.readysidestepl();
            return;
        }
        this.setCurrentAnimation(this.aTurnCCW);
        this.weapon.setCurrentAnimation(this.weapon.aTurnCCW);
        this.play();
        this.state = ArcherState.M_TURNINGCCW;
        this.sTurn.play();
    }

    public void ActionDown() {
        // If in survival, change color on backswipe
        if(GameConstants.gameMode == GameConstants.GameMode.SURVIVAL){
            this.changeColorBackward();
            return;
        }
        if(this.attackMode()){
            this.unready();
            return;
        }
        this.setCurrentAnimation(this.aTurnCW);
        this.weapon.setCurrentAnimation(this.weapon.aTurnCW);
        this.play();
        this.state = ArcherState.M_TURNINGBACKWARDS;
        this.sTurn.play();
    }

    @Override
    protected void idle() {
        this.setCurrentAnimation(this.aIdle);
        this.weapon.setCurrentAnimation(this.weapon.aIdle);
        this.play();
        this.state = ArcherState.M_IDLE;
    }

    @Override
    protected void updateMesh(){
        super.updateMesh();
        this.weapon.updateMesh();
    }

    @Override
    protected void play(){
        super.play();
        this.weapon.play();
    }

    private void ready(){
        this.setCurrentAnimation(this.aReady);
        this.weapon.setCurrentAnimation(this.weapon.aReady);
        this.play();
        this.state = ArcherState.READY;
        this.sReady.play();
    }

    private void unready(){
        this.setCurrentAnimation(this.aUnready);
        this.weapon.setCurrentAnimation(this.weapon.aUnready);
        this.play();
        this.state = ArcherState.UNREADY;
        this.sReady.play();
    }

    private void readyidle() {
        this.setCurrentAnimation(this.aReadyIdle);
        this.weapon.setCurrentAnimation(this.weapon.aReadyIdle);
        this.play();
        this.state = ArcherState.A_IDLE;
    }

    private void readysidestepl(){
        this.setCurrentAnimation(this.aReadySidestepL);
        this.weapon.setCurrentAnimation(this.weapon.aReadySidestepL);
        this.play();
        this.state = ArcherState.A_SIDESTEP_L;
        this.sMove.play();
    }

    private void readysidestepr(){
        this.setCurrentAnimation(this.aReadySidestepR);
        this.weapon.setCurrentAnimation(this.weapon.aReadySidestepR);
        this.play();
        this.state = ArcherState.A_SIDESTEP_R;
        this.sMove.play();
    }

    private void attack() {
        if (numArrows < 1) {
            this.changeColorForward();
            return;
        }
        this.setCurrentAnimation(this.aAttack);
        this.weapon.setCurrentAnimation(this.weapon.aAttack);
        this.play();
        this.state = ArcherState.A_SHOOT;
    }

    @Override
    protected void draw() {
        MaterialManager.changeMaterialColor("Crossbow", this.weaponColor);
        super.draw();
    }

    protected Animation quickAddAnim(String animationName){
        return super.addAnimation(String.format("%s_%s", animationName, this.weapon.TAG.toLowerCase()));
    }

    private void changeSpeed(){
        this.changeDeltaTimeModifier(super.getPlayerSpeed());
        this.weapon.changeDeltaTimeModifier(super.getPlayerSpeed());
        this.aIdle.deltaTimeModifier = 1.f;
        this.aReadyIdle.deltaTimeModifier = 1.f;
        this.aAttack.deltaTimeModifier = 1.f;
        this.weapon.aIdle.deltaTimeModifier = 1.f;
        this.weapon.aReadyIdle.deltaTimeModifier = 1.f;
        this.weapon.aAttack.deltaTimeModifier = 1.f;
    }

    @Override
    protected void changeColorForward(){
        super.changeColorForward();
        this.sChangeColor.play();
    }

    @Override
    protected void changeColorBackward(){
        super.changeColorBackward();
        this.sChangeColor.play();
    }

}

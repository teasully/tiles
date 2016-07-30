package project.etrumper.thomas.ghostbutton;

/**
 * Created by thoma on 6/7/2016.
 */
public class CubeMonster extends ChessPiece {

    enum MonsterStance{
        PASSIVE,
        NORMAL,
        AGGRESSIVE
    }

    // Monster attributes
    MonsterStance stance;
    int aggroDistance = 50,
        chargeDistance = 50;

    Animation aWalkBegin, aWalkEnd;

    boolean changed;

    int travelDistance;

    boolean charging;

    PieceDirection permanentDirection;

    Sound sRope1, sBell1, sBell2, sDeath,
    sTick1, sTick2;

    CubeMonster(PieceDirection permanentDirection){
        super("CubeMonster", PieceDirection.UP, PieceType.ENTITY);
        this.init(permanentDirection);
    }

    CubeMonster(int x, int y, PieceDirection permanentDirection) {
        super("CubeMonster", PieceDirection.UP, PieceType.ENTITY, x, y);
        this.init(permanentDirection);
    }

    private void init(PieceDirection permanentDirection){
        super.MAXTASKS = 4;

        super.aIdle = addAnimation("idle");
        super.aMove = addAnimation("move");
        super.aMove.addAnimationEvent("move_forward", 24);
        this.aWalkBegin = addAnimation("move_begin");
        this.aWalkEnd = addAnimation("move_end");
        this.aWalkEnd.addAnimationEvent("can_move", 16);
        super.aDeath = addAnimation("death");

        super.rotation = new float[]{270f, 270f, 0f};

        this.stance = MonsterStance.NORMAL;
        super.idle();

        this.changed = true;

        this.travelDistance = 0;

        this.charging = false;
        // Get random color
        int i = Math.abs(SuperManager.r.nextInt() % GameConstants.Number_Colors);
        switch (i) {
            case (0):
                this.weaponColor = WeaponColor.RED;
                break;
            case (1):
                this.weaponColor = WeaponColor.GREEN;
                break;
            case (2):
                this.weaponColor = WeaponColor.BLUE;
                break;
        }
        this.changeDeltaTimeModifier(this.getSpeed());
        // Check if has a permanent cameraDirection
        if(permanentDirection != null){
            this.permanentDirection = permanentDirection;
        }else{
            this.permanentDirection = null;
        }
        // Load sounds
        /*this.sRope1 = super.loadSound(R.raw.rope_creak1);
        this.sBell1 = super.loadSound(R.raw.bell1);
        this.sBell2 = super.loadSound(R.raw.bell2);
        this.sDeath = super.loadSound(R.raw.pinball_bell);
        this.sTick1 = super.loadSound(R.raw.tick1);
        this.sTick2 = super.loadSound(R.raw.tick2);*/
    }

    @Override
    protected void update() {
        super.update();
        // Check if player is controlling
        if(playerControlled && !charging){
            this.changeDeltaTimeModifier(super.getPlayerSpeed());
        }
        if (this.currentAnimation == aIdle) {
            super.idle();
            this.brainstuff();
        } else if (this.canMove()) {
            if (this.currentAnimation == this.aDeath) {
                return;
            } else if (this.currentAnimation == this.aWalkBegin) {
                if (super.canMoveForward()) {
                    this.currentAnimation = this.aMove;
                }else{
                    this.handleObjects("player_check");
                }
                this.play();
            } else if (this.currentAnimation == this.aMove) {
                this.playSoundMove();
                // Move camera if player controlled
                if(playerControlled){
                    this.moveCameraTo();
                }
                // Move model's position
                super.ActionUp();
                // If it has travel distance, continue movement and iterate
                if (this.travelDistance > 0) {
                    if (super.canMoveForward()) {
                        this.currentAnimation = this.aMove;
                        this.travelDistance--;
                    } else {
                        this.handleObjects("player_check");
                    }
                }else{
                    this.walkend();
                }
                this.play();
            } else if (this.currentAnimation == this.aWalkEnd) {
                super.idle();
            }
        }
        if (super.hasTask()) {
            PieceTask task = super.tasks.get(0);
            if(task == PieceTask.ACTION){
                super.tasks.remove(0);
                super.changeColorForward();
            } else if (super.canMove()) {
                super.tasks.remove(0);
                // Resolve rotation
                super.resolveRotationTo(task);
                // Begin roll
                this.setCurrentAnimation(this.aWalkBegin);
                this.play();
            } else if (this.currentAnimation == this.aMove || this.currentAnimation == this.aWalkBegin) {
                // If is facing up
                if (task == PieceTask.UP) {
                    if (this.direction == PieceDirection.UP) {
                        this.travelDistance++;
                        tasks.remove(0);
                    }
                } else if (task == PieceTask.DOWN) {
                    if (this.direction == PieceDirection.DOWN) {
                        this.travelDistance++;
                        tasks.remove(0);
                    }
                } else if (task == PieceTask.LEFT) {
                    if (this.direction == PieceDirection.LEFT) {
                        this.travelDistance++;
                        tasks.remove(0);
                    }
                } else if (task == PieceTask.RIGHT) {
                    if (this.direction == PieceDirection.RIGHT) {
                        this.travelDistance++;
                        tasks.remove(0);
                    }
                }
            }
        }
        this.updateMesh();
        // Don't allow animation to move cube forward while dying
        if (this.currentAnimation == this.aDeath) {
            return;
        }
        String[] animTasks = super.getAnimationEvents();
        if (animTasks != null) {
            for (String eventName : animTasks) {
                switch (eventName) {
                    case ("move_forward"):
                        // Handle collisions
                        this.handleObjects(eventName);
                        // Move internal tile position
                        super.moveForwardOnBoard();
                        break;
                }
            }
        }
    }

    protected void brainstuff() {
        // Get a random number to create impulse
        int i = Math.abs(SuperManager.r.nextInt() % 150);
        if (i < 4) {
            // Check if has permanent cameraDirection
            if(this.permanentDirection != null){
                if(this.permanentDirection == PieceDirection.LEFT){
                    i = 0;
                }else if(this.permanentDirection == PieceDirection.RIGHT){
                    i = 1;
                }else if(this.permanentDirection == PieceDirection.UP){
                    i = 2;
                }else{
                    i = 3;
                }
            }
            Tile[] tiles = GameConstants.tileMap.getSurroundingTiles(this.tilePos);
            if (i == 0) {
                if (super.canMove(tiles[0])) {
                    super.queueTask(PieceTask.LEFT);
                }
            } else if (i == 1) {
                if (super.canMove(tiles[1])) {
                    super.queueTask(PieceTask.RIGHT);
                }
            } else if (i == 2) {
                if (super.canMove(tiles[2])) {
                    super.queueTask(PieceTask.UP);
                }
            } else if (i == 3) {
                if (super.canMove(tiles[3])) {
                    super.queueTask(PieceTask.DOWN);
                }
            }
            this.travelDistance = i % 3;
            // Check if should charge by checking stance
            if (this.stance == MonsterStance.PASSIVE) {
                // If passive, only go after across piece if facing
                ChessPiece gotPiece = GameConstants.tileMap.getAcrossPiece(this);
                if (gotPiece.TAG.equals("Player") || gotPiece.TAG.equals("Barricade")) {
                    // Check distance
                    if (TileMap.getDistance(this.tilePos, gotPiece.tilePos) < this.aggroDistance) {
                        // Charge
                        this.charge();
                    }
                }
            } else if (this.stance == MonsterStance.NORMAL) {
                // If normal, go after across piece if lined up in any cameraDirection.. unless has permanent cameraDirection
                ChessPiece gotPiece;
                i = 4;
                if(this.permanentDirection != null){
                    i = 1;
                    super.resolveRotationTo(this.permanentDirection);
                }
                for (int u = 0; u < i; u++) {
                    gotPiece = GameConstants.tileMap.getAcrossPiece(this);
                    // Check distance against internal variable so can't charge across map
                    if (TileMap.getDistance(this.tilePos, gotPiece.tilePos) < this.aggroDistance) {
                        // If the piece across is player, charge and break for loop
                        if (gotPiece.TAG.equals("Player") || gotPiece.TAG.equals("Barricade")) {
                            // Charge
                            this.charge();
                            break;
                        }
                        // Check if cube in front of self is charging, so follow
                        else if(gotPiece.TAG.equals(this.TAG)){
                            CubeMonster cubeMonster = (CubeMonster) gotPiece;
                            if(cubeMonster.charging){
                                this.charge();
                                break;
                            }
                        }
                    }
                    // Else, rotate to new cameraDirection and repeat search
                    super.ActionLeft();
                }
            }

        } else {
            return;
        }
        // Begin roll
        this.setCurrentAnimation(this.aWalkBegin);
        this.play();
    }

    protected void handleObjects(String event) {
        ChessPiece acrossPiece = GameConstants.tileMap.getAcrossPiece(this, 1);
        // Check if the current event is move_forward (checking halfway through move animation)
        if (event.equals("move_forward")) {
            // Check if empty tile
            if (acrossPiece == null) {
                return;
            }
            // If charging, check for entities
            if (this.charging) {
                // Check if same color
                if (acrossPiece.TAG.equals("Player") || acrossPiece.weaponColor == this.weaponColor) {
                    acrossPiece.remove(this);
                }
                // If hit a barricade, hurt it.
                else if (acrossPiece.TAG.equals("Barricade")) {
                    GameConstants.Barricade_Health--;
                    this.walkend();
                }
            }
        }
        // Or check before moving
        else if (event.equals("player_check")) {
            // Check for null; already failed canMove() so walkend(); Tile must be claimed and awaiting piece
            if (acrossPiece == null) {
                this.walkend();
            }
            // Check if charging and player
            else if (this.charging && acrossPiece.TAG.equals("Player")) {
                this.currentAnimation = this.aMove;
            }
            // If hit a barricade, hurt it.
            else {
                this.walkend();
                if (acrossPiece.TAG.equals("Barricade")) {
                    GameConstants.Barricade_Health--;
                }
            }
        }
    }

    @Override
    protected void remove(ChessPiece removeBy){
        GameConstants.tileMap.addGarbage(this);
        // Find rotation
        PieceDirection gDirection = removeBy.direction;
        PieceTask sendTask = null;
        if(gDirection == PieceDirection.LEFT){
            sendTask = PieceTask.LEFT;
        }else if(gDirection == PieceDirection.RIGHT){
            sendTask = PieceTask.RIGHT;
        }else if(gDirection == PieceDirection.UP){
            sendTask = PieceTask.UP;
        }else if(gDirection == PieceDirection.DOWN){
            sendTask = PieceTask.DOWN;
        }
        super.resolveRotationTo(sendTask);
        // Set animations
        super.setCurrentAnimation(this.aDeath);
        super.play();
        // Play sound
        this.sDeath.play();
    }

    private void charge(){
        // Increase movement speed to look as if speeding towards target
        super.changeDeltaTimeModifier(this.getSpeed() * 3f);
        this.charging = true;
        // Increase time till charge
        this.aWalkBegin.deltaTimeModifier = 0.25f;
        // Play charge sound
        super.playSoundBasedOnCamera(this.sRope1);
        // Allow a charge distance of 5 tiles
        this.travelDistance = this.chargeDistance;
    }

    private void playSoundMove(){
        int i = Math.abs(SuperManager.r.nextInt() % 2);
        Sound s;
        switch(i){
            case(0):
                s = this.sTick1;
                super.playSoundBasedOnCamera(s);
                break;
            case(1):
                s = this.sTick2;
                super.playSoundBasedOnCamera(s);
                break;
        }
    }

    private void playSoundHit(){
        int i = Math.abs(SuperManager.r.nextInt() % 2);
        LOGE(i+"");
        Sound s;
        switch(i){
            case(0):
                s = this.sBell1;
                super.playSoundBasedOnCamera(s);
                break;
            case(1):
                s = this.sBell2;
                super.playSoundBasedOnCamera(s);
                break;
        }
    }

    private void uncharge(){
        this.changeDeltaTimeModifier(this.getSpeed());
        this.charging = false;
        this.aWalkBegin.deltaTimeModifier = 1.f;
        if(this.travelDistance > 0){
            this.playSoundHit();
            GameConstants.camera.startShake(75);
        }
    }

    protected float getSpeed(){
        if(this.playerControlled){
            return super.getPlayerSpeed();
        }
        if(this.weaponColor == WeaponColor.RED){
            return 0.5f;
        }else if(this.weaponColor == WeaponColor.GREEN){
            return 1.f;
        }else{
            return 1.5f;
        }
    }

    private void walkend(){
        this.currentAnimation = this.aWalkEnd;
        if(this.charging){
            this.uncharge();
        }
        this.travelDistance = 0;
    }

    @Override
    protected void changeDeltaTimeModifier(float newTime){
        super.changeDeltaTimeModifier(newTime);
        // Keep some animations the same speed
        this.aWalkBegin.deltaTimeModifier = 1.f;
        this.aWalkEnd.deltaTimeModifier = 1.f;
    }

    @Override
    protected void draw(){
        MaterialManager.changeMaterialColor("Cube", this.weaponColor);
        super.draw();
    }

}

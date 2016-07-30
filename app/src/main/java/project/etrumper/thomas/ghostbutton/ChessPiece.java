package project.etrumper.thomas.ghostbutton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thoma on 4/25/2016.
 */

public abstract class ChessPiece extends BasicEntity {

    enum PieceDirection{
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    enum PieceTask{
        UP,
        DOWN,
        LEFT,
        RIGHT,
        ACTION
    }

    enum PieceType{
        ENTITY,
        ITEM,
        STATIONARY
    }

    enum WeaponColor{
        RED,
        BLUE,
        GREEN
    }

    WeaponColor weaponColor;

    PieceTask taskOverflow;

    Animation aMove, aTurnCW, aTurnCCW, aIdle, aAttack, aDeath;

    PieceDirection direction;

    PieceType type;

    ChessPiece[] children;

    int tilePos[];

    boolean playerControlled;

    List<PieceTask> tasks;
    int MAXTASKS = 1;

    ChessPiece(String libraryName, PieceDirection direction, PieceType type) {
        super(libraryName);

        this.init(direction, type);
    }

    ChessPiece(String libraryName, PieceDirection direction, PieceType type, int x, int y) {
        super(libraryName);

        this.init(direction, type);

        this.moveByBoardTo(x, y, 0);
    }

    protected void ActionUp() {
        if (this.direction == PieceDirection.UP) {
            this.position[1] += GameConstants.tileMap.tileWidth;

            return;
        }
        if (this.direction == PieceDirection.DOWN) {
            this.position[1] -= GameConstants.tileMap.tileWidth;

            return;
        }
        if (this.direction == PieceDirection.LEFT) {
            this.position[0] += GameConstants.tileMap.tileWidth;

            return;
        }
        this.position[0] -= GameConstants.tileMap.tileWidth;
    }

    protected void addChild(ChessPiece piece){
        for(ChessPiece piece1 : children){
            if(piece.ID == piece1.ID){
                LOGE("Trying to add duplicate chess piece");
                return;
            }
        }
        ChessPiece[] np = new ChessPiece[this.children.length + 1];
        System.arraycopy(this.children, 0, np, 0, this.children.length);
        this.children = np;
        this.children[this.children.length - 1] = piece;
    }

    protected boolean canMove(Tile tile){
        return (tile != null && tile.getPiece() == null);
    }

    protected boolean canMove(){
        if(currentAnimation.getAnimationName().equals(aIdle.getAnimationName())){
            return true;
        }
        return !currentAnimation.isPlaying;
    }

    protected boolean canMoveBackward() {   // L R U D
        Tile[] tiles = GameConstants.tileMap.getSurroundingTiles(this.tilePos);
        int i = 1;
        if (this.direction == PieceDirection.DOWN) {
            i = 2;
        }
        if (this.direction == PieceDirection.UP) {
            i = 3;
        }
        if (this.direction == PieceDirection.RIGHT) {
            i = 0;
        }
        return (tiles[i] != null && tiles[i].getPiece() == null && tiles[i].claim(this.ID));
    }

    protected boolean canMoveForward() {
        Tile[] tiles = GameConstants.tileMap.getSurroundingTiles(this.tilePos);
        int i = 0;
        if (this.direction == PieceDirection.DOWN) {
            i = 3;
        }
        if (this.direction == PieceDirection.UP) {
            i = 2;
        }
        if (this.direction == PieceDirection.RIGHT) {
            i = 1;
        }
        return canMove(tiles[i]) && tiles[i].claim(this.ID);
    }

    private void init(PieceDirection direction, PieceType type){
        this.direction = direction;
        this.type = type;

        this.children = new ChessPiece[0];

        this.tilePos = new int[2];

        this.position = new float[]{0f, 0f, GameConstants.zDepth};
        this.rotation = new float[]{270f, 0f, 0f};

        this.tasks = new ArrayList<>(0);
        this.taskOverflow = null;

        this.playerControlled = false;
    }

    protected void remove(ChessPiece removedBy){
        GameConstants.tileMap.removePiece(this);
    }

    protected void resolveRotationTo(PieceTask task){
        if (task == PieceTask.UP) {
            if (this.direction == PieceDirection.DOWN) {
                this.ActionRight();
                this.ActionRight();
            } else if (this.direction == PieceDirection.LEFT) {
                this.ActionLeft();
            } else if (this.direction == PieceDirection.RIGHT) {
                this.ActionRight();
            }
        } else if (task == PieceTask.DOWN) {
            if (this.direction == PieceDirection.UP) {
                this.ActionLeft();
                this.ActionLeft();
            } else if (this.direction == PieceDirection.LEFT) {
                this.ActionRight();
            } else if (this.direction == PieceDirection.RIGHT) {
                this.ActionLeft();
            }
        } else if (task == PieceTask.LEFT) {
            if (this.direction == PieceDirection.RIGHT) {
                this.ActionLeft();
                this.ActionLeft();
            } else if (this.direction == PieceDirection.DOWN) {
                this.ActionLeft();
            } else if (this.direction == PieceDirection.UP) {
                this.ActionRight();
            }
        } else if (task == PieceTask.RIGHT) {
            if (this.direction == PieceDirection.LEFT) {
                this.ActionLeft();
                this.ActionLeft();
            } else if (this.direction == PieceDirection.DOWN) {
                this.ActionRight();
            } else if (this.direction == PieceDirection.UP) {
                this.ActionLeft();
            }
        }
    }

    protected void resolveRotationTo(PieceDirection direction){
        if (direction == PieceDirection.UP) {
            if (this.direction == PieceDirection.DOWN) {
                this.ActionRight();
                this.ActionRight();
            } else if (this.direction == PieceDirection.LEFT) {
                this.ActionLeft();
            } else if (this.direction == PieceDirection.RIGHT) {
                this.ActionRight();
            }
        } else if (direction == PieceDirection.DOWN) {
            if (this.direction == PieceDirection.UP) {
                this.ActionLeft();
                this.ActionLeft();
            } else if (this.direction == PieceDirection.LEFT) {
                this.ActionRight();
            } else if (this.direction == PieceDirection.RIGHT) {
                this.ActionLeft();
            }
        } else if (direction == PieceDirection.LEFT) {
            if (this.direction == PieceDirection.RIGHT) {
                this.ActionLeft();
                this.ActionLeft();
            } else if (this.direction == PieceDirection.DOWN) {
                this.ActionLeft();
            } else if (this.direction == PieceDirection.UP) {
                this.ActionRight();
            }
        } else if (direction == PieceDirection.RIGHT) {
            if (this.direction == PieceDirection.LEFT) {
                this.ActionLeft();
                this.ActionLeft();
            } else if (this.direction == PieceDirection.DOWN) {
                this.ActionRight();
            } else if (this.direction == PieceDirection.UP) {
                this.ActionLeft();
            }
        }
    }

    protected ChessPiece[] getAllChildren(){
        if(this.children.length == 0){
            return null;
        }
        ChessPiece[] returnPieces = this.children;
        // Get children's children
        for(ChessPiece child : this.children){
            ChessPiece[] gotChildren = child.getAllChildren();
            // Add children
            if(gotChildren != null){
                ChessPiece[] newChildren = new ChessPiece[returnPieces.length + gotChildren.length];
                System.arraycopy(returnPieces, 0, newChildren, 0, returnPieces.length);
                System.arraycopy(gotChildren, 0, newChildren, returnPieces.length, gotChildren.length);
                returnPieces = newChildren;
            }
        }
        return returnPieces;
    }

    protected void moveBackward(){
        if(this.direction == PieceDirection.DOWN){
            this.position[1] += GameConstants.tileMap.tileWidth;
            return;
        }
        if(this.direction == PieceDirection.UP){
            this.position[1] -= GameConstants.tileMap.tileWidth;
            return;
        }
        if(this.direction == PieceDirection.LEFT){
            this.position[0] -= GameConstants.tileMap.tileWidth;
            return;
        }
        this.position[0] += GameConstants.tileMap.tileWidth;
    }

    protected void ActionLeft() {
        this.rotation[1] -= 90f;
        if (this.direction == PieceDirection.DOWN) {
            this.direction = PieceDirection.LEFT;
        } else if (this.direction == PieceDirection.UP) {
            this.direction = PieceDirection.RIGHT;
        } else if (this.direction == PieceDirection.LEFT) {
            this.direction = PieceDirection.UP;
        } else {
            this.direction = PieceDirection.DOWN;
        }
    }

    protected void ActionRight(){
        this.rotation[1] += 90f;
        if(this.direction == PieceDirection.DOWN){
            this.direction = PieceDirection.RIGHT;
            return;
        }
        if(this.direction == PieceDirection.UP){
            this.direction = PieceDirection.LEFT;
            return;
        }
        if(this.direction == PieceDirection.LEFT){
            this.direction = PieceDirection.DOWN;
            return;
        }
        this.direction = PieceDirection.UP;
    }

    protected void moveByBoardTo(int ... tilePos){
        // 3rd int sent upon ChessPiece constructor so error messages not produced
        if(tilePos.length == 2) {
            if (this.type == PieceType.ITEM) {
                GameConstants.tileMap.removeItem(this);
            } else {
                GameConstants.tileMap.removePiece(this);
            }
        }
        this.tilePos = new int[]{tilePos[0], tilePos[1]};
        float[] pos = GameConstants.tileMap.getGlobalPosition(this.tilePos[0], this.tilePos[1]);
        this.position = new float[]{ pos[0], pos[1], this.position[2]};
        if(this.type == PieceType.ITEM){
            GameConstants.tileMap.addItem(this);
        }else {
            GameConstants.tileMap.addPiece(this);
        }
    }

    protected void moveForwardOnBoard() {
        //SuperManager.tileMap.map[this.tilePos[0]][this.tilePos[1]].piece = null;
        GameConstants.tileMap.removePiece(this);
        if (this.direction == PieceDirection.DOWN) {
            this.tilePos[1]--;
        }else if (this.direction == PieceDirection.UP) {
            this.tilePos[1]++;
        }else if (this.direction == PieceDirection.LEFT) {
            this.tilePos[0]++;
        }else {
            this.tilePos[0]--;
        }
        GameConstants.tileMap.addPiece(this);
        //SuperManager.tileMap.map[this.tilePos[0]][this.tilePos[1]].piece = this;
    }

    protected void moveBackwardOnBoard() {
        GameConstants.tileMap.removePiece(this);
        if (this.direction == PieceDirection.DOWN) {
            this.tilePos[1]++;
        }else if (this.direction == PieceDirection.UP) {
            this.tilePos[1]--;
        }else if (this.direction == PieceDirection.LEFT) {
            this.tilePos[0]--;
        }else {
            this.tilePos[0]++;
        }
        GameConstants.tileMap.addPiece(this);
    }

    protected void idle(){
        this.currentAnimation = this.aIdle;
        this.play();
    }

    protected void playSoundBasedOnCamera(Sound sound) {
        float distanceToMakeSound = 16f;
        // Check object is not too far to make sound
        int[] cameraPos = GameConstants.tileMap.getTilePosition(GameConstants.camera.position);
        if (TileMap.getDistance(cameraPos, this.tilePos) > distanceToMakeSound) {
            //LOGE("Sound too far away");
            return;
        }
        float left = 1f,
                right = 1f;
        double yDist = TileMap.getYDistance(cameraPos, this.tilePos);
               // xDist = TileMap.getXDistance(cameraPos, this.tilePos);
        //left += 0.5f * (xDist * -1.f / distanceToMakeSound);
        //right += 0.5f * (xDist / distanceToMakeSound);
        // Apply y distance
        left -= ((float) yDist / distanceToMakeSound);
        right -= ((float) yDist / distanceToMakeSound);
        if (left < 0f) {
            left = 0f;
        }
        if (right < 0f) {
            right = 0f;
        }
        //LOGE(String.format("Playing %f %f", left, right));
        SoundManager.soundPool.play(sound.soundID, left, right, 0, 0, 1.f);
    }

    protected boolean queueTask(PieceTask task){
        if(this.tasks.toArray().length < MAXTASKS) {
            this.tasks.add(task);
            return true;
        }
        this.taskOverflow = task;
        return false;
    }

    protected int queueTasks(PieceTask ... tasks){
        int numberQeueud = 0;
        boolean loop = true;
        for(; numberQeueud < tasks.length && loop; numberQeueud++){
            loop = queueTask(tasks[numberQeueud]);
        }
        return numberQeueud;
    }

    protected boolean hasTask(){
        return (this.tasks.size() > 0);
    }

    protected String[] getAnimationEvents(){
        String[] events = new String[0];
        for(Animation animation : this.animations){
            if(animation == null || animation.events == null)
                continue;
            for(AnimationEvent event : animation.events){
                String eventName = null;
                if(!event.handled && event.fired){
                    event.handled = true;
                    eventName = event.name;
                }
                if(eventName != null){
                    String[] newEvents = new String[events.length + 1];
                    System.arraycopy(events, 0, newEvents, 0, events.length);
                    newEvents[events.length] = eventName;
                    events = newEvents;
                }
            }
        }
        if(events.length > 0){
            return events;
        }
        return null;
    }

    protected Sound loadSound(int rID){
        Sound s = null;
        try {
            s = SoundManager.loadSound(rID);
        }catch(MandatoryException e){
            LOGE(e.toString());
        }
        return s;
    }

    protected void moveCameraTo(){
        int[] offset = new int[]{0, 0};
        int offsetAmount = 2; // Amount moved forward by
        // If survival, move camera more in front of player
        if(GameConstants.gameMode == GameConstants.GameMode.SURVIVAL) {
            if (GameConstants.cameraTilt) {
                offsetAmount = 2;
            } else {
                offsetAmount = 8;
            }
        }
        if(this.direction == PieceDirection.UP){
            offset[1] = offsetAmount;
        }else if(this.direction == PieceDirection.DOWN){
            offset[1] = -offsetAmount;
        }else if(this.direction == PieceDirection.LEFT){
            offset[0] = offsetAmount;
        }else{
            offset[0] = -offsetAmount;
        }
        float[] gotPos = GameConstants.tileMap.getGlobalPosition(this.tilePos[0] + offset[0], this.tilePos[1] + offset[1]);
        GameConstants.camera.startEase(new float[]{-gotPos[0], -gotPos[1], GameConstants.camera.position[2]}, 1000);
    }

    protected float getPlayerSpeed() {
        switch (Overlay.optionsMenu.getData()[1]) {
            // Slow
            case (0):
                return 1f;
            // Normal
            case (1):
                return 2.f;
            // Fast
            case (2):
                return 3.f;
            // Faaaast
            case (3):
                return 5.f;
        }
        return -1f;
    }

    protected void changeColorForward(){
        // switch the color number
        switch(GameConstants.Number_Colors) {
            // Do nothing
            case(1):
                this.weaponColor = WeaponColor.RED;
                break;
            // cycle through available colors
            case(2):
                if (this.weaponColor == WeaponColor.RED) {
                    this.weaponColor = WeaponColor.GREEN;
                } else if (this.weaponColor == WeaponColor.GREEN) {
                    this.weaponColor = WeaponColor.RED;
                }
                break;
            case (3):
                if (this.weaponColor == WeaponColor.RED) {
                    this.weaponColor = WeaponColor.GREEN;
                } else if (this.weaponColor == WeaponColor.GREEN) {
                    this.weaponColor = WeaponColor.BLUE;
                } else {
                    this.weaponColor = WeaponColor.RED;
                }
                break;
        }
    }

    protected void changeColorBackward(){
        // switch the color number
        switch(GameConstants.Number_Colors) {
            // Do nothing
            case(1):
                this.weaponColor = WeaponColor.RED;
                break;
            // cycle through available colors
            case(2):
                if (this.weaponColor == WeaponColor.RED) {
                    this.weaponColor = WeaponColor.GREEN;
                } else if (this.weaponColor == WeaponColor.GREEN) {
                    this.weaponColor = WeaponColor.RED;
                }
                break;
            case (3):
                if (this.weaponColor == WeaponColor.RED) {
                    this.weaponColor = WeaponColor.BLUE;
                } else if (this.weaponColor == WeaponColor.GREEN) {
                    this.weaponColor = WeaponColor.RED;
                } else {
                    this.weaponColor = WeaponColor.GREEN;
                }
                break;
        }
    }

    protected void queueTaskBasedOnDirection(){
        if(this.direction == PieceDirection.UP){
            this.queueTask(PieceTask.UP);
        }else if(this.direction == PieceDirection.DOWN){
            this.queueTask(PieceTask.DOWN);
        }else if(this.direction == PieceDirection.LEFT){
            this.queueTask(PieceTask.LEFT);
        }else{
            this.queueTask(PieceTask.RIGHT);
        }
    }

    @Override
    protected void update(){
        super.update();
        for(ChessPiece piece : this.children){
            piece.update();
        }
    }

    @Override
    protected void draw(){
        super.draw();
        for(ChessPiece piece : this.children){
            piece.draw();
        }
    }

}

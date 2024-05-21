package name.panitz.game2d.klaus;

class Level {
		private String levelData;
		private Level next; 
		Level(String levelData) { this.levelData = levelData; }
		public void setNextLevel(Level next) { this.next = next; }
		public void loadNextLevel() {  } 
        public String getLevelData(){return levelData;}
        public Level getNextLevel(){return next;}
}
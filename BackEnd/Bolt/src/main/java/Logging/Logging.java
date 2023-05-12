package Logging;


public class Logging {
    public static void printColored(String text,Color color){
        switch (color){
            case RED:
                System.out.print("\u001B[31m"+text);
                break;
            case GREEN:
                System.out.print("\u001B[32m"+text);
                break;
            case YELLOW:
                System.out.print("\u001B[33m"+text);
                break;
            case BLUE:
                System.out.print("\u001B[34m"+text);
                break;
            case PURPLE:
                System.out.print("\u001B[35m"+text);
                break;
            case CYAN:
                System.out.print("\u001B[36m"+text);
                break;
            case WHITE:
                System.out.print("\u001B[37m"+text);
                break;
        }
    }
}


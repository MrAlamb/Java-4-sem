import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

import static java.lang.Math.random;
import static java.lang.Math.round;

public class BouncingBall implements Runnable {
    private static final int MAX_RADIUS = 40;
    private static final int MIN_RADIUS = 3;
    private static final int MAX_SPEED = 15;
    private final Field field;

    private final int radius;

    private volatile boolean mousePressed = false;
    private double lastX, lastY, lastPressTime;


    private final Color color;
    // Текущие координаты мяча
    private double x;
    private double y;
    // Вертикальная и горизонтальная компонента скорости
    private int speed;
    private double speedX;
    private double speedY;
    // Конструктор класса BouncingBall
    public BouncingBall(Field field) {


// через getWidth(), getHeight()
        this.field = field;

        radius = (int)(random()*(MAX_RADIUS - MIN_RADIUS)) + MIN_RADIUS;

        speed = Double.valueOf(round((float) (5 * MAX_SPEED) / radius)).intValue();
        if (speed>MAX_SPEED) {speed = MAX_SPEED;
        }
// Начальное направление скорости тоже случайно,
// угол в пределах от 0 до 2PI
        double angle = random()*2*Math.PI;
// Вычисляются горизонтальная и вертикальная компоненты скорости
        speedX = 3*Math.cos(angle);
        speedY = 3*Math.sin(angle);
// Цвет мяча выбирается случайно
        color = new Color((float) random(), (float) random(),
                (float) random());
// Начальное положение мяча случайно
        x = random()*(field.getSize().getWidth()-2*radius) + radius;
        y = random()*(field.getSize().getHeight()-2*radius) + radius; // Создаѐм новый экземпляр потока, передавая аргументом
// ссылку на класс, реализующий Runnable (т.е. на себя)
        Thread thisThread = new Thread(this);

        field.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (ballContainsPoint(e.getX(), e.getY())) {
                    mousePressed = true;
                    lastX = e.getX();
                    lastY = e.getY();
                    lastPressTime = System.currentTimeMillis();
                    speedX = 0;
                    speedY = 0;
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (mousePressed) {
                    mousePressed = false;
                    double newX = e.getX();
                    double newY = e.getY();
                    speedX = (newX - lastX) / (e.getWhen() - lastPressTime);
                    speedY = (newY - lastY) / (e.getWhen() - lastPressTime);
                }
            }
        });


        thisThread.start();
    }
    private boolean ballContainsPoint(double x, double y) {
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2)) <= radius;
    }

    // Метод run() исполняется внутри потока. Когда он завершает работу, // то завершается и поток
    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public void run() {
        try {
            while(true) {

                field.canMove();
                if (x + speedX <= radius) {
// Достигли левой стенки, отскакиваем право
                    speedX = -speedX;
                    x = radius;
                } else
                if (x + speedX >= field.getWidth() - radius) {
                    // Достигли правой стенки, отскок влево
                    speedX = -speedX;
                    x= Double.valueOf(field.getWidth() - radius).intValue(); } else
                if (y + speedY <= radius) {
// Достигли верхней стенки
                    speedY = -speedY;
                    y = radius;
                } else
                if (y + speedY >= field.getHeight() - radius) {
                    // Достигли нижней стенки
                    speedY = -speedY;
                    y = Double.valueOf(field.getHeight()-radius).intValue(); } else {
                    // Просто смещаемся
                    x += speedX;
                    y += speedY;
                }
                Thread.sleep(16-speed);
            }
        } catch (InterruptedException ignored) {

        } }


    public void paint(Graphics2D canvas) {
        canvas.setColor(color);
        canvas.setPaint(color);
        Ellipse2D.Double ball = new Ellipse2D.Double(x-radius, y-radius,2*radius, 2*radius);
        canvas.draw(ball);
        canvas.fill(ball);
    }
}
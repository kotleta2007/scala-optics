import monocle.Iso
import scala.math

sealed trait Complex:
    def add(that: Complex): Complex
    def sub(that: Complex): Complex
    def mul(that: Complex): Complex
    def div(that: Complex): Complex

    lazy val x: Double
    lazy val y: Double
    lazy val r: Double
    lazy val a: Double

val iso = 
    def norm(x1: Double, x2: Double) = math.sqrt(x1*x1 + x2*x2)
    def toPolar(z: ComplexCartesian) = 
        ComplexPolar(
            norm(z.x, z.y), 
            math.atan2(z.y, z.x)
        )
    def toCartesian(z: ComplexPolar) = 
        ComplexCartesian(
            z.r * math.cos(z.a), 
            z.r * math.sin(z.a)
        )
    Iso[ComplexCartesian, ComplexPolar](toPolar)(toCartesian)

case class ComplexCartesian(_x: Double, _y: Double) extends Complex:
    override def add(that: Complex): Complex = 
        ComplexCartesian(
            this.x + that.x,
            this.y + that.y
        )
    override def sub(that: Complex): Complex = 
        ComplexCartesian(
            this.x - that.x,
            this.y - that.y
        )
    override def mul(that: Complex): Complex = iso.get(this) mul that
    override def div(that: Complex): Complex = iso.get(this) div that

    override lazy val x: Double = _x
    override lazy val y: Double = _y
    override lazy val r: Double = iso.get(this).r
    override lazy val a: Double = iso.get(this).a
    
case class ComplexPolar(_r: Double, _a: Double) extends Complex:
    override def add(that: Complex): Complex = iso.reverseGet(this) add that
    override def sub(that: Complex): Complex = iso.reverseGet(this) sub that
    override def mul(that: Complex): Complex = 
        ComplexPolar(
            this.r * that.r,
            this.a + that.a
        )
    override def div(that: Complex): Complex = 
        ComplexPolar(
            this.r / that.r,
            this.a - that.a
        )

    override lazy val x: Double = iso.reverseGet(this).x
    override lazy val y: Double = iso.reverseGet(this).y
    override lazy val r: Double = _r
    override lazy val a: Double = _a

// TESTING

val z1 = ComplexCartesian(1.0, 2.0)
val z2 = ComplexCartesian(3.0, 4.0)
val z3 = ComplexPolar(1.0, 0.0)
val z4 = ComplexPolar(1.0, math.Pi)

z1 add z2
z1 mul z2

z1 sub z3
z2 div z4


fun id(x)=x

fun square(x)=x*x

fun minus(x,y)=x-y

fun k1(x,y)=x

fun k2(x,y)=y

fun stack()=64-2*4+3*5-1-2*14

fun add5(x1,x2,x3,x4,x5)=x1+x2+x3+x4+x5
fun add6(x1,x2,x3,x4,x5,x6)=x1+x2+x3+x4+x5+x6
fun add8(x1,x2,x3,x4,x5,x6,x7,x8)=x1+x2+x3+x4+x5+x6+x7+x8

fun get6(x1,x2,x3,x4,x5,x6,x7,x8)=x6
fun get7(x1,x2,x3,x4,x5,x6,x7,x8)=x7
fun get8(x1,x2,x3,x4,x5,x6,x7,x8)=x8

fun fac(x)=if x=0 then 1 else x*fac(x-1)

fun not(x)=if x then 0 else 1

fun facIt(x)=
  {r := 1
  ;while not(x=0)
   do
     {r := r*x
     ;x := x-1
     }
   ;r}
     
fun or(x,y)=if x then 1 else y

fun fib(x) =if or(x=1,x=0) then x else fib(x-2)+fib(x-1)
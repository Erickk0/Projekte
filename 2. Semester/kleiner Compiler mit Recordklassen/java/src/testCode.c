#include <stdio.h>
long int id(long int x);

long int square(long int x);

long int minus(long int x,long int y);

long int k1(long int x,long int y);

long int k2(long int x,long int y);

long int stack();

long int add5(long int x1,long int x2,long int x3,long int x4,long int x5);
long int add6(long int x1,long int x2,long int x3,long int x4,long int x5,long int x6);
long int add8(long int x1,long int x2,long int x3,long int x4,long int x5,long int x6,long int x7,long int x8);
long int get6(long int x1,long int x2,long int x3,long int x4,long int x5,long int x6,long int x7,long int x8);
long int get7(long int x1,long int x2,long int x3,long int x4,long int x5,long int x6,long int x7,long int x8);
long int get8(long int x1,long int x2,long int x3,long int x4,long int x5,long int x6,long int x7,long int x8);

long int fac(long int x);

long int not(long int x);

long int facIt(long int x);
     
long int or(long int x,long int y);

long int fib(long int x);

  
int main(){
  printf("id(42) = %ld\n",id(42));
  printf("square(5) = %ld\n",square(5));
  printf("minus(17,4) = %ld\n",minus(17,4));
  printf("k1(42,24) = %ld\n",k1(42,24));
  printf("k2(2,42) = %ld\n",k2(2,42));
  printf("64-2*4+3*5-1-2*14 = %ld\n",stack());

  printf("add5(1,2,3,4,5) = %ld\n",add5(1,2,3,4,5));
  printf("add6(1,2,3,4,5,6) = %ld\n",add6(1,2,3,4,5,6));
  printf("add8(1,2,3,4,5,6,7,8) = %ld\n",add8(1,2,3,4,5,6,7,8));
  printf("get6(1,2,3,4,5,6,7,8) = %ld\n",get6(1,2,3,4,5,6,7,8));
  printf("get7(1,2,3,4,5,6,7,8) = %ld\n",get7(1,2,3,4,5,6,7,8));
  printf("get8(1,2,3,4,5,6,7,8) = %ld\n",get8(1,2,3,4,5,6,7,8));
  
  printf("fac(5) = %ld\n",fac(5));
  printf("facIt(5) = %ld\n",facIt(5));
  printf("fib(10) = %ld\n",fib(10));
  return 0;
}
  
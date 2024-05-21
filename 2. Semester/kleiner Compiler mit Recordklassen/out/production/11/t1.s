

.globl  id
id:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  subq $16, %rsp
  movq -8(%rbp), %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  square
square:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  subq $16, %rsp
  movq -8(%rbp), %rax
  pushq %rax
  movq -8(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  imulq %rbx, %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  minus
minus:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  movq %rsi, -16(%rbp)
  subq $24, %rsp
  movq -8(%rbp), %rax
  pushq %rax
  movq -16(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  subq %rbx, %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  k1
k1:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  movq %rsi, -16(%rbp)
  subq $24, %rsp
  movq -8(%rbp), %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  k2
k2:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  movq %rsi, -16(%rbp)
  subq $24, %rsp
  movq -16(%rbp), %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  stack
stack:
  pushq %rbp
  movq %rsp, %rbp
  subq $8, %rsp
  movq $64, %rax
  pushq %rax
  movq $2, %rax
  pushq %rax
  movq $4, %rax
  movq %rax, %rbx
  popq %rax
  imulq %rbx, %rax
  movq %rax, %rbx
  popq %rax
  subq %rbx, %rax
  pushq %rax
  movq $3, %rax
  pushq %rax
  movq $5, %rax
  movq %rax, %rbx
  popq %rax
  imulq %rbx, %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq $1, %rax
  movq %rax, %rbx
  popq %rax
  subq %rbx, %rax
  pushq %rax
  movq $2, %rax
  pushq %rax
  movq $14, %rax
  movq %rax, %rbx
  popq %rax
  imulq %rbx, %rax
  movq %rax, %rbx
  popq %rax
  subq %rbx, %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  add5
add5:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  movq %rsi, -16(%rbp)
  movq %rdx, -24(%rbp)
  movq %rcx, -32(%rbp)
  movq %r8, -40(%rbp)
  subq $48, %rsp
  movq -8(%rbp), %rax
  pushq %rax
  movq -16(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq -24(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq -32(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq -40(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  add6
add6:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  movq %rsi, -16(%rbp)
  movq %rdx, -24(%rbp)
  movq %rcx, -32(%rbp)
  movq %r8, -40(%rbp)
  movq %r9, -48(%rbp)
  subq $56, %rsp
  movq -8(%rbp), %rax
  pushq %rax
  movq -16(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq -24(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq -32(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq -40(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq -48(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  add8
add8:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  movq %rsi, -16(%rbp)
  movq %rdx, -24(%rbp)
  movq %rcx, -32(%rbp)
  movq %r8, -40(%rbp)
  movq %r9, -48(%rbp)
  subq $56, %rsp
  movq -8(%rbp), %rax
  pushq %rax
  movq -16(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq -24(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq -32(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq -40(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq -48(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq 16(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  pushq %rax
  movq 24(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  get6
get6:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  movq %rsi, -16(%rbp)
  movq %rdx, -24(%rbp)
  movq %rcx, -32(%rbp)
  movq %r8, -40(%rbp)
  movq %r9, -48(%rbp)
  subq $56, %rsp
  movq -48(%rbp), %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  get7
get7:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  movq %rsi, -16(%rbp)
  movq %rdx, -24(%rbp)
  movq %rcx, -32(%rbp)
  movq %r8, -40(%rbp)
  movq %r9, -48(%rbp)
  subq $56, %rsp
  movq 16(%rbp), %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  get8
get8:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  movq %rsi, -16(%rbp)
  movq %rdx, -24(%rbp)
  movq %rcx, -32(%rbp)
  movq %r8, -40(%rbp)
  movq %r9, -48(%rbp)
  subq $56, %rsp
  movq 24(%rbp), %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  fac
fac:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  subq $16, %rsp
  movq -8(%rbp), %rax
  pushq %rax
  movq $0, %rax
  movq %rax, %rbx
  popq %rax
  cmpq %rbx, %rax
  sete %al
  movzbq %al, %rax
  cmpq $0, %rax
  je L0
  movq -8(%rbp), %rax
  pushq %rax
  movq -8(%rbp), %rax
  pushq %rax
  movq $1, %rax
  movq %rax, %rbx
  popq %rax
  subq %rbx, %rax
  movq %rax, %rdi
  call fac
  movq %rax, %rbx
  popq %rax
  imulq %rbx, %rax
  jmp L1
  L0:
  movq $1, %rax
  L1:
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  not
not:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  subq $16, %rsp
  movq -8(%rbp), %rax
  cmpq $0, %rax
  je L2
  movq $1, %rax
  jmp L3
  L2:
  movq $0, %rax
  L3:
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  facIt
facIt:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  subq $24, %rsp
  movq $1, %rax
  movq %rax, %rbx
  movq -16(%rbp), %rax
  movq %rbx, (%rax)
  popq %rax
  jmp L4
  L5:
  movq -16(%rbp), %rax
  pushq %rax
  movq -8(%rbp), %rax
  movq %rax, %rbx
  popq %rax
  imulq %rbx, %rax
  movq %rax, %rbx
  movq -16(%rbp), %rax
  movq %rbx, (%rax)
  popq %rax
  movq -8(%rbp), %rax
  pushq %rax
  movq $1, %rax
  movq %rax, %rbx
  popq %rax
  subq %rbx, %rax
  movq %rax, %rbx
  movq -8(%rbp), %rax
  movq %rbx, (%rax)
  L4:
  movq -8(%rbp), %rax
  pushq %rax
  movq $0, %rax
  movq %rax, %rbx
  popq %rax
  cmpq %rbx, %rax
  sete %al
  movzbq %al, %rax
  movq %rax, %rdi
  call not
  cmpq $0, %rax
  jne L5
  popq %rax
  movq -16(%rbp), %rax
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  or
or:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  movq %rsi, -16(%rbp)
  subq $24, %rsp
  movq -8(%rbp), %rax
  cmpq $0, %rax
  je L6
  movq -16(%rbp), %rax
  jmp L7
  L6:
  movq $1, %rax
  L7:
  movq %rbp, %rsp
  popq %rbp
  ret

.globl  fib
fib:
  pushq %rbp
  movq %rsp, %rbp
  movq %rdi, -8(%rbp)
  subq $16, %rsp
  movq -8(%rbp), %rax
  pushq %rax
  movq $1, %rax
  movq %rax, %rbx
  popq %rax
  cmpq %rbx, %rax
  sete %al
  movzbq %al, %rax
  movq %rax, %rdi
  movq -8(%rbp), %rax
  pushq %rax
  movq $0, %rax
  movq %rax, %rbx
  popq %rax
  cmpq %rbx, %rax
  sete %al
  movzbq %al, %rax
  movq %rax, %rsi
  call or
  cmpq $0, %rax
  je L8
  movq -8(%rbp), %rax
  pushq %rax
  movq $2, %rax
  movq %rax, %rbx
  popq %rax
  subq %rbx, %rax
  movq %rax, %rdi
  call fib
  pushq %rax
  movq -8(%rbp), %rax
  pushq %rax
  movq $1, %rax
  movq %rax, %rbx
  popq %rax
  subq %rbx, %rax
  movq %rax, %rdi
  call fib
  movq %rax, %rbx
  popq %rax
  addq %rbx, %rax
  jmp L9
  L8:
  movq -8(%rbp), %rax
  L9:
  movq %rbp, %rsp
  popq %rbp
  ret

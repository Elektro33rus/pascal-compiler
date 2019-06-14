@.strln = private unnamed_addr constant [2 x i8] c"\0A\00"

@.strint = private unnamed_addr constant [4 x i8] c"%i\0A\00"

@.strfloat = private unnamed_addr constant [4 x i8] c"%f\0A\00"

declare i32 @printf(i8*, ...)

@"x" = internal global i32 undef

define i32 @main() {
br label %1

;Label %1
%2 = alloca i32
store i32 9, i32* %2
%3 = alloca i32
store i32 8, i32* %3
%4 = load i32, i32* %2
%5 = load i32, i32* %3
%6 = icmp slt i32 %4, %5
br i1 %6, label %7, label %$WHILE0WHILE$

;Label %7 AND
%8 = alloca i32
store i32 7, i32* %8
%9 = alloca i32
store i32 6, i32* %9
%10 = load i32, i32* %8
%11 = load i32, i32* %9
%12 = icmp slt i32 %10, %11
br i1 %12, label %13, label %$WHILE1WHILE$

;Label %13 OR
%14 = alloca i32
store i32 5, i32* %14
%15 = alloca i32
store i32 4, i32* %15
%16 = load i32, i32* %14
%17 = load i32, i32* %15
%18 = icmp slt i32 %16, %17
br i1 %18, label %19, label %23

;Label %19
%20 = alloca i32
store i32 50, i32* %20
%21 = load i32, i32* %20
%22 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strint, i32 0, i32 0), i32 %21)
br label %13

;Label %23
ret i32 0
}
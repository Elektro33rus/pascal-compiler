@.strln = private unnamed_addr constant [2 x i8] c"\0A\00"

@.strint = private unnamed_addr constant [4 x i8] c"%i\0A\00"

@.strfloat = private unnamed_addr constant [4 x i8] c"%f\0A\00"

declare i32 @printf(i8*, ...)

define i32 @Func0(i32){
%2 = alloca i32
store i32 %0, i32* %2
%3 = alloca i32
store i32 1, i32* %3
%4 = load i32, i32* %2
%5 = load i32, i32* %3
%6 = icmp sle i32 %4, %5
br i1 %6, label %7, label %10

;Label %7 ifthen
%8 = alloca i32
store i32 1, i32* %8
%9 = load i32, i32* %8
store i32 %9, i32* %2
br label %25

;Label %10 ifelse
%11 = alloca i32
store i32 1, i32* %11
%12 = load i32, i32* %2
%13 = load i32, i32* %11
%14 = sub i32 %12, %13
%15 = alloca i32
store i32 %14, i32* %15
%16 = load i32, i32* %15
store i32 %16, i32* %15
%17 = load i32, i32* %2
%18 = call i32 @Func0(i32 %15)
%19 = alloca i32
store i32 %18, i32* %19
%20 = load i32, i32* %17
%21 = load i32, i32* %19
%22 = mul i32 %20, %21
%23 = alloca i32
store i32 %22, i32* %23
%24 = load i32, i32* %23
store i32 %24, i32* %2
br label %25

;Label %25 ifend
%26 = load i32, i32* %2
store i32 %26, i32* %2
%27 = load i32, i32* %2
ret i32 %27
}


define i32 @main() {
%1 = alloca i32
store i32 10, i32* %1
%2 = load i32, i32* %1
store i32 %2, i32* %1
%3 = load i32, i32* %1
%4 = call i32 @Func0(i32 %3)
%5 = alloca i32
store i32 %4, i32* %5
%6 = load i32, i32* %5
store i32 %6, i32* %5
%7 = load i32, i32* %5
%8 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strint, i32 0, i32 0), i32 %7)
ret i32 0
}
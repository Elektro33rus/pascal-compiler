@.strln = private unnamed_addr constant [2 x i8] c"\0A\00"

@.strint = private unnamed_addr constant [4 x i8] c"%i\0A\00"

@.strfloat = private unnamed_addr constant [4 x i8] c"%f\0A\00"

declare i32 @printf(i8*, ...)


define i32 @main() {
%1 = alloca i32
store i32 0, i32* %1
%2 = load i32, i32* %1
store i32 %2, i32* %1
br label %3

;Label %3
%4 = alloca i32
store i32 10, i32* %4
%5 = load i32, i32* %1
%6 = load i32, i32* %4
%7 = icmp slt i32 %5, %6
br i1 %7, label %8, label %23

;Label %8
%9 = alloca i32
store i32 1, i32* %9
%10 = load i32, i32* %9
%11 = load i32, i32* %1
%12 = add i32 %10, %11
%13 = alloca i32
store i32 %12, i32* %13
%14 = load i32, i32* %13
store i32 %14, i32* %1
%15 = alloca i32
store i32 5, i32* %15
%16 = load i32, i32* %1
%17 = load i32, i32* %15
%18 = icmp eq i32 %16, %17
br i1 %18, label %19, label %20

;Label %19 ifthen
br label %3

;Label %20 ifend
%21 = load i32, i32* %1
%22 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strint, i32 0, i32 0), i32 %21)
br label %3

;Label %23
ret i32 0
}
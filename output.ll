@.strln = private unnamed_addr constant [2 x i8] c"\0A\00"

@.strint = private unnamed_addr constant [4 x i8] c"%i\0A\00"

@.strfloat = private unnamed_addr constant [4 x i8] c"%f\0A\00"

declare i32 @printf(i8*, ...)

@"x" = internal global i32 undef
define i32 @Func0(){
%"maerresult" = alloca i32
@"z" = alloca i32
%1 = alloca i32
store i32 3, i32* %1
%2 = alloca i32
store i32 4, i32* %2
%3 = load i32, i32* %1
%4 = load i32, i32* %2
%5 = icmp slt i32 %3, %4
br i1 %5, label %6, label %9

;Label %6 ifthen
%7 = alloca i32
store i32 333, i32* %7
%8 = load i32, i32* %7
store i32 %8, i32* @"z"
br label %12

;Label %9 ifelse
%10 = alloca i32
store i32 124, i32* %10
%11 = load i32, i32* %10
store i32 %11, i32* @"z"
br label %12

;Label %12 ifend
%13 = load i32, i32* @"z"
%14 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strint, i32 0, i32 0), i32 %13)
ret i32 0
}


define i32 @main() {
%1 = call i32 @Func0()
%2 = alloca i32
store i32 %1, i32* %2
ret i32 0
}
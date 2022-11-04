library(data.table)
library("dplyr")
library("lubridate")
library(UBL)
library(xgboost)
library(tidytable)
library(mltools)
library(caret)
library(DescTools)

setwd("C:/Users/AntunesPedro/Documents/R/trabalho_DF/")

########################################################
# Undersampling

train <- fread("train.csv", sep=",", header=TRUE)
test <- fread("test.csv", sep=",", header=TRUE)

ids <- sample(1:nrow(train), nrow(train))
subtrain <- as.data.frame(train[ids,])


subtrain_isFraud <- filter(subtrain, subtrain$isFraud==1)

subtrain_notFraud <- filter(subtrain, subtrain$isFraud==0)
ids_notFraud <- sample(1:nrow(subtrain_notFraud), nrow(subtrain_isFraud))
subtrain_notFraud <- as.data.frame(subtrain_notFraud[ids_notFraud,])

subtrain <- rbind(subtrain_isFraud, subtrain_notFraud)
#######################################################
# remove columns with % of NA > 5%

subtrain[subtrain==''] <- NA
data_na <- (colSums(is.na(subtrain)) / nrow(subtrain)) * 100
d <- data_na[data_na <5]

a <- attributes(d)
subtrain <- subset(subtrain, select=unlist(a))
a <- a$names[-2] # removing isFraud because test doesnt have this feature
test <- subset(test, select=unlist(a))

######################################################
# Modify NAs values to statistic functions


for(var in 1:ncol(subtrain)) {
  if (class(subtrain[,var]) == "numeric") {
    subtrain[is.na(subtrain[,var]), var] <- mean(subtrain[,var], na.rm = TRUE)
  }
  else if (class(subtrain[,var]) %in% c("character", "factor")) {
    subtrain[is.na(subtrain[,var]), var] <- Mode(subtrain[,var], na.rm = TRUE)
  }
}

#for(var1 in 1:ncol(test)) {
#  if (class(test[,var1]) == "numeric") {
#    test[is.na(test[,var1]), var1] <- mean(test[,var1], na.rm = TRUE)
#  }
#  else if (class(test[,var1]) %in% c("character", "factor")) {
#    test[is.na(test[,var1]), var1] <- Mode(test[,var1], na.rm = TRUE)
#  }
#}

#####################################################
# dealing with card1-card6

subtrain$card1[is.na(subtrain$card1)] <- 0
subtrain$card2[is.na(subtrain$card2)] <- 0
subtrain$card3[is.na(subtrain$card3)] <- 0
subtrain$card5[is.na(subtrain$card5)] <- 0

subtrain$card4[is.na(subtrain$card4)] <- ''
subtrain$card6[is.na(subtrain$card6)] <- ''


test$card1[is.na(test$card1)] <- 0
test$card2[is.na(test$card2)] <- 0
test$card3[is.na(test$card3)] <- 0
test$card5[is.na(test$card5)] <- 0

test$card4[is.na(test$card4)] <- ''
test$card6[is.na(test$card6)] <- ''


#subtrain <- subtrain %>% mutate(card=group_indices(subtrain, card1, card2, card3, card4, card5, card6))
#test <- test %>% mutate(card=group_indices(test, card1, card2, card3, card4, card5, card6))
#subtrain$card <- paste(subtrain$card1, subtrain$card2, subtrain$card3, subtrain$card4, subtrain$card5, subtrain$card6, sep='_')
#test$card <- paste(test$card1, test$card2, test$card3, test$card4, test$card5, test$card6, sep='_')

#######################################################
# TransactionDT add hour, wday, mday

day0 <- dmy_hms("1/1/2020 00:00:00 GMT")

subtrain <- mutate(subtrain, hour=hour(day0+seconds(TransactionDT)))
subtrain <- mutate(subtrain, mday=mday(day0+seconds(TransactionDT)))
subtrain <- mutate(subtrain, wday=wday(day0+seconds(TransactionDT)))

test <- mutate(test, hour=hour(day0+seconds(TransactionDT)))
test <- mutate(test, mday=mday(day0+seconds(TransactionDT)))
test <- mutate(test, wday=wday(day0+seconds(TransactionDT)))

#######################################################
#######################################################
#######################################################
# normalizing C1-C14

#subtrain$C1 <- subtrain$C1 / max(subtrain$C1)
#subtrain$C2 <- subtrain$C2 / max(subtrain$C2)
#subtrain$C3 <- subtrain$C3 / max(subtrain$C3)
#subtrain$C4 <- subtrain$C4 / max(subtrain$C4)
#subtrain$C5 <- subtrain$C5 / max(subtrain$C5)
#subtrain$C6 <- subtrain$C6 / max(subtrain$C6)
#subtrain$C7 <- subtrain$C7 / max(subtrain$C7)
#subtrain$C8 <- subtrain$C8 / max(subtrain$C8)
#subtrain$C9 <- subtrain$C9 / max(subtrain$C9)
#subtrain$C10 <- subtrain$C10 / max(subtrain$C10)
#subtrain$C11 <- subtrain$C11 / max(subtrain$C11)
#subtrain$C12 <- subtrain$C12 / max(subtrain$C12)
#subtrain$C13 <- subtrain$C13 / max(subtrain$C13)
#subtrain$C14 <- subtrain$C14 / max(subtrain$C14)

#subtrain$D1 <- subtrain$D1 / max(subtrain$D1)


#test$C1 <- test$C1 / max(test$C1)
#test$C2 <- test$C2 / max(test$C2)
#test$C3 <- test$C3 / max(test$C3)
#test$C4 <- test$C4 / max(test$C4)
#test$C5 <- test$C5 / max(test$C5)
#test$C6 <- test$C6 / max(test$C6)
#test$C7 <- test$C7 / max(test$C7)
#test$C8 <- test$C8 / max(test$C8)
#test$C9 <- test$C9 / max(test$C9)
#test$C10 <- test$C10 / max(test$C10)
#test$C11 <- test$C11 / max(test$C11)
#test$C12 <- test$C12 / max(test$C12)
#test$C13 <- test$C13 / max(test$C13)
#test$C14 <- test$C14 / max(test$C14)

#test$D1 <- test$D1 / max(test$D1)


#######################################################
#######################################################
#######################################################
# one hot encoding (parse categorical columns to numerical)

dummy <- dummyVars(" ~ .", data=subtrain)
subtrain <- data.frame(predict(dummy, newdata = subtrain)) 

dummy2 <- dummyVars(" ~ .", data=test)
test <- data.frame(predict(dummy2, newdata = test))

# We want to predict a rare case, isFraud==1, only ~3% of the cases
# imbalanced domain
# Missing a prediction of rare cases is worst than missing a normal case

# Balancing the data distribution (isFraud==1 is 50% of the cases)
#datU <- RandUnderClassif(isFraud ~ ., subtrain, C.perc = "balance")

#subtrain <- na.omit(subtrain)

#subtrain$isFraud <- as.factor(subtrain$isFraud)

# removing "TransactionID" and "isFraud" from subtrain
m <- xgboost(data = data.matrix(subtrain[,-c(1,2)]), label = data.matrix(subtrain$isFraud), max.depth = 10, eta = 0.15, nthread = 2, nrounds = 65, objective = "binary:logistic", verbose = 1)
test <- select(test,colnames(subtrain[,-2]))
p <- predict(m, data.matrix(test[,-1]))
head(p)

submission <- data.frame(TransactionID=test$TransactionID, isFraud=p)

if(any(is.na(submission))) submission[is.na(submission)] <- mean(submission$isFraud)

write.csv(submission, file="submission.csv", row.names=FALSE)

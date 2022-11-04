# Privacy Systems in the Cloud

This POC was built in JAVA with jdk_11.

The main package is com.company

## KMS SERVER
The KMS Server is the class com.company.Server and it is responsible to handler system entities requests and to store domains system.

## Entities

### Host

Host is the class com.company.components.Host and it does encrypt and decrypt request.

### HSM

HSM is the class com.company.componentes.Hsm and it is responsible to ensure safety for entire system. It is trusted anchor to verify, sign, encrypt and decrypt data.

### Operator

Operator is the class com.company.components.Operator and it is responsible to manage system domains. It have an importante role when it creates new domains, because we are assuming that this operation is trusted.

# How to compile

Run the following commands to compile java classes:
```
	cd src
	javac -cp . com/company/*.java com/company/components/*.java com/company/keys_management/*.java com/company/crypto/*.java
```

# How to run

Run the following commands to lauch entities and KMS.

## Operator

```
	cd src
	java -cp . com.company.components.Operator
```

After run the commands, you need to identify the operator by an operator name.
To the system work, you need to start operator name by "operator-".
For example, "operator-1".

## HSM

```
	cd src
	java -cp . com.company.components.Hsm
```

After run the commands, you need to identify the hsm by an hsm name.
To the system work, you need to start hsm name by "hsm-".
For example, "hsm-2".

## Host
```
	cd src
	java -cp . com.company.components.Host
```

After run the commands, you need to identify the host by an host name.
To the system work, you need to start host name by "host-" and then an number. This number is important because it was used to handler domains on KMS.
For example, "host-1".

## Server
```
	cd src
	java -cp . com.company.Server
```
After run the commands, you have your KMS Server booted and listening for request by system entities.

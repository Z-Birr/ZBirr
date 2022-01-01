from django.db import models
from django.contrib.auth.models import User

class Transactions(models.Model):
    sender = models.ForeignKey(User, on_delete=models.CASCADE, related_name="sender", null=True)
    reciever = models.ForeignKey(User, on_delete=models.CASCADE, related_name="reciever", null=True)
    amount = models.DecimalField(max_digits=10, decimal_places=2)
    date = models.DateTimeField(auto_now_add=True)
    senderBalance = models.DecimalField(max_digits=10, decimal_places=2)
    recieverBalance = models.DecimalField(max_digits=10, decimal_places=2)

class Balance(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE, primary_key=True)
    currentBalance = models.DecimalField(max_digits=10, decimal_places=2)

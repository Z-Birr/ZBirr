from django.contrib import admin
from users.models import Balance, Transactions
# Register your models here.

admin.site.register(Balance)
admin.site.register(Transactions)
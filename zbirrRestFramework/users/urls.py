from django.conf.urls import url
from users import views
from django.urls import path, include

urlpatterns = [
    # url(r'^users/$', views.all_users),
    # url(r'^user/(?P<pk>[a-zA-Z0-9]+)/$', views.user_detail),
    # url(r'^transactions/$', views.all_transactions),
    # url(r'^transaction/(?P<pk>[0-9]+)', views.transaction_detail),
    # url(r'^balance/$', views.balance),
    # url(r'^balance/(?P<pk>[a-zA-Z0-9]+)', views.balance_detail),
    url(r'^transfer/$', views.transfer, name="transfer"),
    path('rest-auth/', include('rest_auth.urls')),
    path('rest-auth/registration/', include('rest_auth.registration.urls')),
    path('initialize/', views.initialize, name="initialize"),
    path('transactiontable/', views.transactionTable, name="transactionTable"),
    path('currentbalance/', views.currentBalance, name="currentBalance")
]
package com.android.gs.dto;

import java.io.Serializable;

public class CartItemData implements Serializable{

	public int productId;
	public int quantity;
	public CartItemData(int id,int quan){
		productId = id;
		quantity = quan;
	}
}

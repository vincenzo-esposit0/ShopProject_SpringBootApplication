package com.example.shop_project.service;

import com.example.shop_project.entity.Item;
import com.example.shop_project.model.ItemView;
import com.example.shop_project.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<ItemView> getAllItems() {
        return itemRepository.findAll().stream()
                .map(item -> new ItemView(item.getName(), item.getPrice(), item.getQuantity()))
                .collect(Collectors.toList());
    }

    public ItemView getItemById(Long id) {
        return itemRepository.findById(id).map(item -> new ItemView(item.getName(), item.getPrice(), item.getQuantity())).orElse(null);
    }

    public Item createItem(ItemView itemView) {
        Item item = new Item(itemView.getName(), itemView.getPrice(), itemView.getQuantity());
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public boolean itemExists(String name) {
        return itemRepository.existsByName(name);
    }
}

package demo.view.page;

import org.springframework.data.domain.Pageable;

public interface IPageItemTransfer<T> {
    BootstrapPageItem getBootstrapPageItem(Pageable pageable, T item);
}

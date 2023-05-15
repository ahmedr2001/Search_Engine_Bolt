import React from "react";

type Props = {
	onClick: () => {};
	active?: boolean;
	disabled?: boolean;
	children: JSX.Element;
};

export default function NavButtons({ children, onClick, active }: Props) {
	return (
		<button
			className={`flex flex-col cursor-pointer items-center justify-center w-9 h-9 shadow-[0_4px_10px_rgba(0,0,0,0.03)] text-sm font-normal transition-colors rounded-lg
      ${active ? "bg-overlay text-highlight" : "text-highlight"}
      
      `}
			onClick={onClick}>
			{children}
		</button>
	);
}

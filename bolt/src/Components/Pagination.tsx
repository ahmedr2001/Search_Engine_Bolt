import React, { useCallback } from "react";
import { FaChevronLeft, FaChevronRight } from "react-icons/fa";
import NavButtons from "./NavButtons";
import resultType from "../types/resultType";

type Props = {
	currentPage: number;
	totalPages: number;
	setCurrentPage: (x: number) => void;
};

export default function Pagination({
	currentPage,
	totalPages,
	setCurrentPage,
}: Props) {
	const pageNumbers = [];
	for (let i = 1; i <= totalPages; i++) {
		pageNumbers.push(i);
	}
	return (
		<div className=" flex mx-auto py-10 gap-0 items-center justify-center">
			<div
				className={`flex justify-center items-center text-xl rounded-full text-highlight hover:bg-overlay w-9 h-9 ${
					currentPage == 1 ? "cursor-not-allowed" : "cursor-pointer"
				}`}
				onClick={() => setCurrentPage(1)}>
				<FaChevronLeft className="translate-x-1/4" />
				<FaChevronLeft className="-translate-x-1/4" />
			</div>

			<div
				className={`flex justify-center items-center text-xl rounded-full text-highlight hover:bg-overlay w-9 h-9 ${
					currentPage == 1 ? "cursor-not-allowed" : "cursor-pointer"
				}`}
				onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}>
				<FaChevronLeft />
			</div>
			<div className="flex gap-5 mx-5 items-center justify-between">
				<span className=" text-secondary text-5xl font-bold">B</span>
				{pageNumbers
					.slice(
						Math.max(1, currentPage - 2) - 1,
						Math.min(totalPages, Math.max(1, currentPage - 2) + 4)
					)
					.map((pageNumber) => (
						<div
							key={pageNumber}
							className={`flex justify-center items-center text-lg rounded-full text-secondary  w-8 h-8 ${
								currentPage === pageNumber
									? "bg-secondary text-overlay"
									: "cursor-pointer text-res-color border-2 border-highlight hover:bg-res-bg"
							}`}
							onClick={() => setCurrentPage(pageNumber)}>
							{pageNumber}
						</div>
					))}
				<span className="text-secondary text-5xl font-bold">LT</span>
			</div>
			<div
				className={`flex justify-center items-center text-xl rounded-full text-highlight hover:bg-overlay w-9 h-9 ${
					currentPage == totalPages
						? "cursor-not-allowed"
						: "cursor-pointer"
				}`}
				onClick={() =>
					setCurrentPage(Math.min(totalPages, currentPage + 1))
				}>
				<FaChevronRight />
			</div>
			<div
				className={`flex justify-center items-center text-xl rounded-full text-highlight hover:bg-overlay w-9 h-9 ${
					currentPage == totalPages
						? "cursor-not-allowed"
						: "cursor-pointer"
				}`}
				onClick={() => setCurrentPage(totalPages)}>
				<FaChevronRight className="translate-x-1/4" />
				<FaChevronRight className="-translate-x-1/4" />
			</div>
		</div>
	);
}
